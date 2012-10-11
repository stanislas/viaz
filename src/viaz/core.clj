(ns viaz.core
	[:require [clojure.xml :as xml]]
	[:require [clojure.zip :as zip]]
	[:require [clj-time.core :as time]]
	[:require [clj-time.format :as format]]
	[:require [clojure.data.zip.xml :as dzip]]
	[:import [org.joda.time LocalDate ReadableInstant ReadablePeriod ReadablePartial]]
	[:import [org.joda.time.format DateTimeFormatter]]
	)

(def zimbra-base-url "http://zimbra.ergon.ch/home/nanchen/viaz.xml")

(extend-protocol time/DateTimeProtocol
  org.joda.time.LocalDate
  (year [this] (.getYear this))
  (month [this] (.getMonthOfYear this))
  (day [this] (.getDayOfMonth this))
  (day-of-week [this] (.getDayOfWeek this))
  (hour [this] (.getHourOfDay this))
  (minute [this] 0)
  (sec [this] 0)
  (milli [this] 0)
  (after? [this #^ReadableInstant that] (.isAfter this that))
  (before? [this #^ReadableInstant that] (.isBefore this that))
  (plus- [this #^ReadablePeriod period] (.plus this period))
  (minus- [this #^ReadablePeriod period] (.minus this period)))

(def zimbra-day-formatter (format/formatter "MM/dd/yyyy"))

(def viaz-add-day-formatter (format/formatter "dd.MM.yyyy"))

(defn format-day [#^ReadablePartial day #^DateTimeFormatter formatter]
	(.print formatter day))

(defn format-zimbra-day [day]
	(format-day day zimbra-day-formatter))

(defn format-viaz-day [day]
	(format-day day viaz-add-day-formatter))

(defn today [] (LocalDate/now))

(defn plus-days [day n]
	(time/plus day (time/days n)))

(defn zimbra-url [start end]
	(str zimbra-base-url "?start=" start "&end=" end))

(defn compute-duration-in-hour [start end]
	(/ (- end start) 1000.0 60 60))

(defn extract-timestamp [comp-node label]
	(let [extracted-millis (dzip/xml1-> comp-node label (dzip/attr :u))]
		(Long/parseLong extracted-millis)))

(defn extract-appointment [comp-node]
	(let [comp-node-attrs (-> comp-node zip/node :attrs)
		  [project activity] (clojure.string/split (:loc comp-node-attrs) #"/")
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
	(str "viaz_add -d " (format-viaz-day day) " " project " " duration " '#" activity " " name "'"))

(defn request-appointments [day] 
	(let [start (format-zimbra-day day)
		  end (format-zimbra-day (plus-days day 1))
		  appts-xml (zip/xml-zip (xml/parse (zimbra-url start end)))]
		(group-durations (extract-appointments appts-xml))))

(defn generate-viaz-add [day]
	(let [appointments (request-appointments day)]
		(map (partial extract-viaz-add day) appointments)))

(defn generate-viaz-add-relative [relative]
	(generate-viaz-add (plus-days (today) relative)))
