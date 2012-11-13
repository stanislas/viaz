(ns viaz.edge
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:use [ring.util.response :only [response]])
  (:use [net.cgrand.moustache :only [app]])
  (:require [viaz.cal :as cal])
  (:require [viaz.core :as viaz])
  (:require [viaz.render :as render]))

(defn error-with-map [error-map]
	(constantly {:status 404
               :body (str error-map)}))

(defn name-period-handler [name period-expression]
	(try
		(let [period (cal/parse-time-expression period-expression)]
    {:name name :days (viaz/generate-viaz-add-relative name period-expression)}
			)
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

(defonce server (run-jetty main-handler
                           {:port 8001 :join? false}))

