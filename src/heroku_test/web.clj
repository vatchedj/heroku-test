(ns heroku-test.web
  (:require
    [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
    [compojure.handler :refer [site]]
    [compojure.route :as route]
    [clojure.java.io :as io]
    [clojure.java.jdbc :as jdbc]
    [ring.middleware.stacktrace :as trace]
    [ring.middleware.session :as session]
    [ring.middleware.session.cookie :as cookie]
    [ring.adapter.jetty :as jetty]
    [ring.middleware.basic-authentication :as basic]
    #_[cemerick.drawbridge :as drawbridge]
    [environ.core :refer [env]]))

(def pg-db
  {:dbtype     "postgresql"
   :dbname     (env :postgres-db)
   :host       (env :pghost)
   :user       (env :postgres-user)
   :password   (env :postgres-password)
   :ssl        true
   :sslfactory "org.postgresql.ssl.NonValidatingFactory"})

(defn- authenticated? [user pass]
  ;; TODO: heroku config:add REPL_USER=[...] REPL_PASSWORD=[...]
  (= [user pass] [(env :repl-user false) (env :repl-password false)]))

#_(def ^:private drawbridge
  (-> (drawbridge/ring-handler)
      (session/wrap-session)
      (basic/wrap-basic-authentication authenticated?)))

(defroutes app
  #_(ANY "/repl" {:as req}
       (drawbridge req))
  (GET "/" []
    (let [test-val (-> (jdbc/query pg-db ["SELECT user from test"])
                       first)]
      {:status  200
       :headers {"Content-Type" "text/plain"}
       :body    (pr-str ["Hello" :from 'Heroku test-val])}))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           {:status 500
            :headers {"Content-Type" "text/html"}
            :body (slurp (io/resource "500.html"))}))))

(defn wrap-app [app]
  ;; TODO: heroku config:add SESSION_SECRET=$RANDOM_16_CHARS
  (let [store (cookie/cookie-store {:key (env :session-secret)})]
    (-> app
        ((if (env :production)
           wrap-error-page
           trace/wrap-stacktrace))
        (site {:session {:store store}}))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (wrap-app #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
