(defproject viaz "0.2-SNAPSHOT"
  :description "Viaz: create viaz-add entries from zimbra calendar"
  :url "https://github.com/stanislas/viaz"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-time "0.11.0"]
                 [com.stuartsierra/component "0.3.0"]
                 [enlive "1.1.6"]
                 [environ "1.0.1"]
                 [http-kit "2.1.19"]
                 [net.cgrand/moustache "1.1.0"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/data.zip "0.1.1"]
                 [ring/ring-core "1.4.0"]]
  :profiles {:dev
             {:source-paths ["dev"]
              :dependencies [[org.clojure/java.classpath "0.2.2"]
                             [org.clojure/tools.namespace "0.2.11"]]}})
