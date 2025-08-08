(defproject heroku-test "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://heroku-test.herokuapp.com"
  :license {:name "FIXME: choose"
            :url "http://example.com/FIXME"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [compojure "1.7.0"]
                 [ring/ring-jetty-adapter "1.10.0"]
                 [ring/ring-devel "1.10.0"]
                 [ring-basic-authentication "1.1.1"]
                 [environ "1.2.0"]
                 #_[com.cemerick/drawbridge "0.0.7"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.postgresql/postgresql "42.7.1"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.2.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "heroku-test-standalone.jar"
  :profiles {:production {:env {:production true}}})
