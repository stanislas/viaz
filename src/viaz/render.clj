(ns viaz.render
  (:use [net.cgrand.enlive-html
         :only [deftemplate defsnippet content clone-for
                nth-of-type first-child do-> set-attr sniptest at emit*]]))

(def viaz-add-sel [[:.viaz-add (nth-of-type 1)] :> first-child])

(defsnippet viaz-add-model "viaz/html/viaz.html" viaz-add-sel
  [{:keys [text]}]
  [:li] (content text))

(def day-sel {[:.day-title] [[:.viaz-add (nth-of-type 1)]]})

(defsnippet day-model "viaz/html/viaz.html" day-sel
  [{:keys [day viaz-add]} model]
  [:.title]   (content day)
  [:.content] (content (map model data)))