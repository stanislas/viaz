(ns viaz.render
  (:require [viaz.core :as viaz])
  (:use [net.cgrand.enlive-html
         :only [deftemplate defsnippet content clone-for
                nth-of-type first-child do-> set-attr sniptest at emit*]]))

(def viaz-add-sel [[:.viaz-add (nth-of-type 1)] :> first-child])

(defsnippet viaz-add-model "viaz/html/viaz.html" viaz-add-sel
  [{:keys [viaz-add]}]
  [:li] (content viaz-add))

(def day-sel {[:.day-title] [[:.viaz-add (nth-of-type 1)]]})

(defn sum-hours [viazes]
  (reduce + (map :duration viazes)))

(defsnippet day-model "viaz/html/viaz.html" day-sel
  [{:keys [day viaz]} model]
  [:.day-title]  (content (str (viaz/format-viaz-day day) ": total duration " (sum-hours viaz)))
  [:.viaz-add] (content (map model viaz)))

(deftemplate main "viaz/html/viaz.html"
  [ctxt]
  [:h1#main-title] (content (str "viaz_add for " (:main-title ctxt)))
  [:div#viazadd] (content (map #(day-model % viaz-add-model) (:days ctxt))))