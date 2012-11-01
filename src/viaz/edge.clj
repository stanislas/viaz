(ns viaz.edge
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:use [ring.util.response :only [response]])
  (:use [net.cgrand.moustache :only [app]])
  (:require [viaz.cal :as cal])
  (:require [viaz.core :as viaz]))

(defn error-with-map [error-map]
	(constantly {:status 404
               :body (str error-map)}))

(defn name-period-handler [name period-expression]
	(try
		(let [period (cal/parse-time-expression period-expression)]
			(response (str {:name name :days (viaz/generate-viaz-add-relative name period-expression)})))
	 (catch IllegalArgumentException e
	 	(error-with-map {:exception e :message (.getMessage e) :stack (.getStackTrace e)}))))

(def main-handler
  (app
    [name] (constantly {:body name})
    [name period] (constantly (name-period-handler name period))
    [&]        (error-with-map {})))

(defonce server (run-jetty main-handler
                           {:port 8001 :join? false}))

