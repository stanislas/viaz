(ns viaz.system
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [net.cgrand.moustache :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :refer [response]]
            [viaz.core :as viaz]
            [viaz.render :as render]))

(defn error-with-map [error-map]
  (constantly {:status 404
               :body   (str error-map)}))

(defn name-period-handler [zimbra-loader name period-expression]
  (try
    {:name name :days (viaz/generate-viaz-add-relative zimbra-loader name period-expression)}
    (catch IllegalArgumentException e
      (error-with-map {:exception e :message (.getMessage e) :stack (.getStackTrace e)}))))

(defn render [t]
  (apply str t))

(def render-to-response
  (comp response render))

(defn render-main [zimbra-loader name period]
  (let [viazadds (name-period-handler zimbra-loader name period)]
    (render-to-response
      (render/main
        {:main-title name
         :rawviaz    viazadds
         :days       (:days viazadds)}))))

(defn main-handler [zimbra-loader]
  (app
    [name] (fn [req] (render-to-response (render/main {:main-title name})))
    [name period] (fn [req] (render-main zimbra-loader name period))
    [&] (error-with-map {})))

(defrecord HttpServer [port zimbra-loader server]
  component/Lifecycle
  (start [component]
    (let [server (run-jetty (main-handler zimbra-loader) {:port port :join? false})]
      (assoc component :server server)))
  (stop [component]
    (let [server (:server component)]
      (.stop server)
      (assoc component :server nil))))

(defn http-server [port]
  (->HttpServer port nil nil))

(defn system [env]
  (component/system-map
    :zimbra-loader (viaz/->ZimbraHttpLoader
                     (:zimbra-base-url env)
                     (:zimbra-calendar-partial-url env)
                     (:zimbra-client-options env))
    :server (component/using
              (http-server (:http-server-port env))
              [:zimbra-loader])))

(defn enviroment [env]
  (assoc env :http-server-port (Integer/parseInt (env :http-server-port))
             :zimbra-client-options (read-string (env :zimbra-client-options))))

(defn -main []
  (system (enviroment env)))
