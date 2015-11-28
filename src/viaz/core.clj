(ns viaz.core
  (:require [com.stuartsierra.component :as component]
            [clj-time.format :as format]
            [clojure.data.zip.xml :as dzip]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [org.httpkit.client :as http]
            [viaz.cal :as cal])
  (:import [org.joda.time ReadablePartial]
           [org.joda.time.format DateTimeFormatter]))

(def zimbra-day-formatter (format/formatter "MM/dd/yyyy"))

(def viaz-add-day-formatter (format/formatter "dd.MM.yyyy"))

(defn format-day [#^ReadablePartial day #^DateTimeFormatter formatter]
  (.print formatter day))

(defn format-zimbra-day [day]
  (format-day day zimbra-day-formatter))

(defn format-viaz-day [day]
  (format-day day viaz-add-day-formatter))

(defn compute-duration-in-hour [start end]
  (/ (- end start) 1000.0 60 60))

(defn extract-timestamp [comp-node label]
  (let [extracted-millis (dzip/xml1-> comp-node label (dzip/attr :u))]
    (Long/parseLong extracted-millis)))

(defn extract-appointment [comp-node]
  (let [comp-node-attrs (-> comp-node zip/node :attrs)
        [project activity] (clojure.string/split (:loc comp-node-attrs) (re-pattern "/"))
        start (extract-timestamp comp-node :s)
        end (extract-timestamp comp-node :e)]
    [(hash-map
       :name (:name comp-node-attrs)
       :project project
       :activity activity)
     (compute-duration-in-hour start end)]))

(defn extract-appointments [xml-doc]
  (map extract-appointment (dzip/xml-> xml-doc :appt :inv :comp)))

(defn merge-durations [result [appt duration]]
  (let [accumulated-duration (get result appt 0)]
    (assoc result appt (+ accumulated-duration duration))))

(defn group-durations [appointments]
  (map
    (fn [[appt duration]] (assoc appt :duration duration))
    (reduce merge-durations {} appointments)))

(defn extract-viaz-add [day {:keys [name project activity duration]}]
  {:duration duration :viaz-add (str "viaz_add -d " (format-viaz-day day) " " project " " duration " '#" activity " " name "'")})

(defprotocol ZimbraLoader
  (loadxml [this username start end]))

(defrecord ZimbraHttpLoader [zimbra-base-url zimbra-calendar-partial-url http-options]
  ZimbraLoader
  (loadxml [this username start end]
    (let [url (str zimbra-base-url username zimbra-calendar-partial-url "?start=" start "&end=" end)]
      (xml/parse
        (:body @(http/get url http-options)))))
  component/Lifecycle
  (start [c] c)
  (stop [c] c))

(defn request-appointments [zimbra-loader username start-day end-day]
  (let [start (format-zimbra-day start-day)
        end (format-zimbra-day end-day)
        appts-xml (zip/xml-zip (loadxml zimbra-loader username start end))]
    (group-durations (extract-appointments appts-xml))))

(defn generate-viaz-add [zimbra-loader username day]
  (let [start day
        end (cal/plus-days day 1)
        appointments (request-appointments zimbra-loader username start end)]
    {:day day :viaz
          (map (partial extract-viaz-add start) appointments)}))

(defn generate-viaz-add-relative [zimbra-loader username time-expression]
  (let [period (cal/parse-time-expression time-expression)
        days (cal/days period)]
    (map (partial generate-viaz-add zimbra-loader username) days)))
