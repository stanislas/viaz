(ns viaz.edge
  (:require [viaz.core :as viaz]
            [viaz.render :as render]
            [net.cgrand.moustache :refer [app]]
            [ring.util.response :refer [response]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn error-with-map [error-map]
	(constantly {:status 404
               :body (str error-map)}))

(defn name-period-handler [name period-expression]
	(try
    {:name name :days (viaz/generate-viaz-add-relative name period-expression)}
	 (catch IllegalArgumentException e
    (error-with-map {:exception e :message (.getMessage e) :stack (.getStackTrace e)}))))

(defn render [t]
  (apply str t))

(def render-to-response
     (comp response render))

(defn render-main [name period]
  (let [viazadds (name-period-handler name period)]
    (render-to-response
      (render/main
        {:main-title name
         :rawviaz viazadds
         :days (:days viazadds)}))))

(def main-handler
  (app
    [name] (fn [req] (render-to-response (render/main {:main-title name})))
    [name period] (fn [req] (render-main name period))
    [&]        (error-with-map {})))

(defn -main [port]
  (run-jetty main-handler {:port (Integer/parseInt port)}))
