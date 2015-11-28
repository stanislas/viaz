(ns user
  (:require [viaz.edge :as edge]
            [ring.adapter.jetty :as jetty]))

(defn start-server []
  (jetty/run-jetty edge/main-handler {:join? false :port 3000}))

(comment
  (defonce server (start-server)))
