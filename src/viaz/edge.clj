(ns viaz.edge
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:use [ring.util.response :only [response]])
  (:use [net.cgrand.moustache :only [app]]))


(def main-handler
  (app
    [name] (constantly {:body name})
    [name period] (constantly {:body (str "name: " name " ;period=" period)})
    [&]        (constantly {:status 404
               :body "Page Not Found"})))

(defonce server (run-jetty main-handler
                           {:port 8001 :join? false}))

