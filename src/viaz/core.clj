(ns viaz.core
	[:require [clojure.xml :as xml]]
	[:require [clojure.zip :as zip]]
	[:require [clj-time.core :as time]]
	[:require [clj-time.format :as format]]
	)

(def zimbra-day-formater (format/formatter "yyyyMMdd"))

(def zimbra-base-url "http://zimbra.ergon.ch/home/nanchen/viaz.xml?view=day&date=")

(defn absolute-day [relative]
	(let [day (time/plus (time/now) (time/days relative))]
		(format/unparse zimbra-day-formater day)))

(defn zimbra-url [formated-day]
	(str zimbra-base-url formated-day))

(defn relative-zimbra-zip [relative]
	(-> relative absolute-day zimbra-url xml/parse zip/xml-zip))

(defn absolute-zimbra-zip [absolute-day]
	(-> absolute-day zimbra-url xml/parse zip/xml-zip))

(zip/xml-zip (xml/parse "http://zimbra.ergon.ch/home/nanchen/viaz.xml?view=day&date=2012101"))
