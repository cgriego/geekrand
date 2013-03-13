(defproject geekrand "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.6.5"]
                 [lib-noir "0.4.9"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.2"]
                 [ring-server "0.2.8"]
                 [com.taoensso/timbre "1.5.2"]
                 [com.taoensso/tower "1.2.0"]
                 [org.clojure/data.zip "0.1.1"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler geekrand.handler/war-handler
         :init    geekrand.handler/init
         :destroy geekrand.handler/destroy}
  :profiles
  {:production {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}}
   :dev {:dependencies [[ring-mock "0.1.3"]
                        [ring/ring-devel "1.1.8"]]}}
  :min-lein-version "2.0.0")