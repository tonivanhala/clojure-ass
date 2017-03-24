(ns clojure-ass.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
  :history
  (fn [db]
    (:history db)))

(defn- get-track-with-image [track]
  (let [img-uri (->> track
                     :image
                     (filter #(= (:size %) "large"))
                     first
                     :#text)
        t       (select-keys track [:mbid :name :artist])]
    (assoc t :img img-uri)))

(re-frame/reg-sub
  :search-results
  (fn [db]
    (map get-track-with-image (:search-results db))))

(re-frame/reg-sub
  :related-tracks
  (fn [db]
    (map get-track-with-image (:related-tracks db))))

(re-frame/reg-sub
  :route
  (fn [db]
    (:route db)))
