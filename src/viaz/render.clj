(ns viaz.render
  (:require [viaz.core :as viaz]
            [net.cgrand.enlive-html
             :refer [deftemplate defsnippet content clone-for
                     nth-of-type first-child do-> set-attr sniptest at emit*]]))

(defn id [day-index entry-index]
  (str "viaz-" day-index "-" entry-index))

(def viaz-add-sel [[:.viaz-add (nth-of-type 1)] :> first-child])

(defsnippet viaz-add-model "public/viaz.html" viaz-add-sel
            [{:keys [viaz-add id]}]
            [:.viaz-add-label] (do-> (content viaz-add)
                                     (set-attr :id id))
            [:.viaz-copy-button] (set-attr :data-clipboard-target (str "#" id)))

(def day-sel {[:.day-title] [[:.viaz-add (nth-of-type 1)]]})

(defn sum-hours [viazes]
  (reduce + (map :duration viazes)))

(defsnippet day-model "public/viaz.html" day-sel
            [{:keys [day viaz day-index]} model]
            [:.day-title] (content (str (viaz/format-viaz-day day) ": total duration " (sum-hours viaz)))
            [:.viaz-add] (content (map-indexed #(model (assoc %2 :id (id day-index %1))) viaz)))

(deftemplate main "public/viaz.html"
             [ctxt]
             [:h1#main-title] (content (str "Viaz for " (:main-title ctxt)))
             [:div#viazadd] (content (map-indexed #(day-model (assoc %2 :day-index %1) viaz-add-model) (:days ctxt))))
