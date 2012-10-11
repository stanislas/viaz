(ns viaz.core
	[:require [clojure.xml :as xml]]
	[:require [clojure.zip :as zip]]
	[:require [clj-time.core :as time]]
	[:require [clj-time.format :as format]]
	[:require [clojure.data.zip.xml :as dzip]]
	[:import [org.joda.time LocalDate ReadableInstant ReadablePeriod]]
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

(defn absolute-day [relative]
	(let [day (time/plus (time/now) (time/days relative))]
		(format/unparse zimbra-day-formater day)))

(defn zimbra-url [start end]
	(str zimbra-base-url "&start=" start "&end=" end))

(defn relative-zimbra-zip [relative]
	(-> relative absolute-day zimbra-url xml/parse zip/xml-zip))

(defn absolute-zimbra-zip [absolute-day]
	(-> absolute-day zimbra-url xml/parse zip/xml-zip))

(defn compute-duration-in-hour [start end]
	(/ (- end start) 1000.0 60 60))

(defn extract-timestamp [comp-node label]
	(let [extracted-millis (dzip/xml1-> comp-node label (dzip/attr :u))]
		(Long/parseLong extracted-millis)))

(defn extract-appointment [comp-node]
	(let [comp-node-attrs (-> comp-node zip/node :attrs)
		  [project activity] (clojure.string/split (comp-node-attrs :loc) #"/")
		  start (extract-timestamp comp-node :s)
		  end (extract-timestamp comp-node :e)]
		(hash-map
			:name (comp-node-attrs :name)
			:project project
			:activity activity
			:duration (compute-duration-in-hour start end))))

(defn extract-comps [xml-doc]
	(map extract-appointment (dzip/xml-> xml-doc :appt :inv :comp)))

(defn extract-viaz-add [{:keys [name project activity duration]}]
	(str "viaz-add " name project activity duration))

(def ex (zip/xml-zip (xml/parse "file:///Users/stan/Downloads/viaz.xml")))
