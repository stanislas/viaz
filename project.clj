(defproject viaz "0.1"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-time "0.4.4"]
                 [org.clojure/data.zip "0.1.1"]
                 [ring/ring-core "1.1.6"]
                 [ring/ring-jetty-adapter "1.1.6"]
                 [net.cgrand/moustache "1.1.0"]
                 [enlive "1.0.1"]]
  :plugins [[lein-ring "0.7.5"]]
  :ring {:handler viaz.edge/main-handler})
