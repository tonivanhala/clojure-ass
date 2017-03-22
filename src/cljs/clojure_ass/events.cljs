(ns clojure-ass.events
    (:require [re-frame.core :as re-frame]
              [clojure-ass.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
  :name-change
  (fn [db [evt-type new-name]]
    (assoc db :name new-name)))

(re-frame/reg-event-fx
  :new-search
  (fn [{:keys [db]} _]
    (let [search-term (:name db)]
      {:db (update db :history #(conj % search-term))})))
