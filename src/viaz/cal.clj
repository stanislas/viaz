(ns viaz.cal
	[:require [clj-time.core :as time]]
	[:require [clj-time.format :as format]]
	[:import [org.joda.time LocalDate ReadableInstant ReadablePeriod ReadablePartial YearMonth]]
	[:import [org.joda.time.format DateTimeFormatter]])

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

(defn format-day [#^ReadablePartial day #^DateTimeFormatter formatter]
	(.print formatter day))

(defn today [] (LocalDate/now))

(defn plus-days [day n]
	(time/plus day (time/days n)))

(defprotocol ContinuousPeriod "represents a continuous period of days as closed-open intervals"
	(start-day [this])
	(duration [this]))

(defn shift-today [steps size-of-step]
	(plus-days (today) (* steps size-of-step)))

(def size-of-day 1)
(def size-of-week 7)

(defrecord RelativeSingleDay [relative]
	ContinuousPeriod
		(start-day [this] (shift-today (:relative this) size-of-day))
		(duration [_] size-of-day))

(defrecord AbsoluteSingleDay [#^LocalDate day]
	ContinuousPeriod
		(start-day [this] (:day this))
		(duration [_] size-of-day))

(defrecord RelativeWeek [relative]
	ContinuousPeriod
		(start-day [this]
			(let [shifted-today (shift-today (:relative this) size-of-week)
				  diff (- (time/day-of-week shifted-today) 1)]
				(plus-days shifted-today (- diff))))
		(duration [_] size-of-week))

(defn shift-month [months]
	(.plusMonths (YearMonth/now) months))

(defrecord RelativeMonth [relative]
	ContinuousPeriod 
		(start-day [this]
			(let [month (shift-month (:relative this))]
				(.toLocalDate month 1)))
		(duration [this]
			(.. (start-day this) (dayOfMonth) (getMaximumValue))))

(defn end-day [period]
	(plus-days (start-day period) (duration period)))

(defn days [period]
	(let [start-day (start-day period)]
		(map #(plus-days start-day %) (range 0 (duration period)))))

(defn split-time-expression [time-expression]
	(let [[_ relative period] (re-matches #"(-?[0-9])*([a-z])*" time-expression)]
		[(Long/parseLong relative) period]))

(defn parse-time-expression [time-expression]
	(let [[relative period] (split-time-expression time-expression)]
		((case period
			(nil "" "d") ->RelativeSingleDay
			"w" ->RelativeWeek
			"m" ->RelativeMonth) relative)))
