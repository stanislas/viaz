(defproject viaz "0.1"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [clj-time "0.11.0"]
                 [org.clojure/data.zip "0.1.1"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [net.cgrand/moustache "1.1.0"]
                 [enlive "1.1.6"]
                 [clj-http "2.0.0"]]
  :plugins [[lein-ring "0.8.8"]]
  :ring {:handler viaz.edge/main-handler}
  :profiles {:dev
             {:source-paths ["dev"]
              :dependencies [[org.clojure/tools.namespace "0.2.11"]
                             [org.clojure/java.classpath "0.2.2"]
                             [javax.servlet/servlet-api "2.5"]]}})
