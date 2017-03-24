(ns clojure-ass.events
    (:require [re-frame.core :as re-frame]
              [clojure-ass.db :as db]
              [day8.re-frame.http-fx]
              [ajax.core :as ajax]
              [cemerick.url :refer [url-encode]]
              [clojure-ass.api-key :refer [API-KEY]]))

(def BASE-URI (str "http://ws.audioscrobbler.com/2.0/?format=json&api_key=" API-KEY))

(defn mk-search-uri [term]
  (str BASE-URI "&method=track.search&track=" (url-encode term)))

(defn mk-get-request [uri success-evt fail-evt]
  {:method            :get
   :uri               uri
   :timeout           8000
   :response-format   (ajax/json-response-format {:keywords? true})
   :on-success        [success-evt]
   :on-failure        [fail-evt]})

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
      {:db (update db :history #(conj % search-term))
       :http-xhrio (mk-get-request (mk-search-uri search-term)
                                   :search-success
                                   :search-fail)})))

(re-frame/reg-event-db
  :search-success
  (fn [db [_ result]]
    (assoc db :search-results (get-in result [:results
                                              :trackmatches
                                              :track] []))))
