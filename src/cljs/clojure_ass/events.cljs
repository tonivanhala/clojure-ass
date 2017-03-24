(ns clojure-ass.events
    (:require [re-frame.core :as re-frame]
              [clojure-ass.db :as db]
              [day8.re-frame.http-fx]
              [ajax.core :as ajax]
              [cemerick.url :refer [url-encode]]
              [clojure-ass.api-key :refer [API-KEY]]
              [bidi.bidi :refer [path-for match-route]]
              [clojure-ass.routes :refer [routes]]))

(def BASE-URI (str "http://ws.audioscrobbler.com/2.0/?format=json&api_key=" API-KEY))

(defn mk-search-uri [term]
  (str BASE-URI "&method=track.search&track=" (url-encode term)))

(defn- mk-related-uri [mbid]
  (str BASE-URI "&method=track.getsimilar&mbid=" mbid))

(defn mk-get-request [uri success-evt fail-evt]
  {:method            :get
   :uri               uri
   :timeout           8000
   :response-format   (ajax/json-response-format {:keywords? true})
   :on-success        [success-evt]
   :on-failure        [fail-evt]})

(defn- initial-dispatch [route]
  (case (:handler route)
    :related {:dispatch [:related-search (get-in route [:route-params :mbid])]}
    {}))

(re-frame/reg-event-fx
  :initialize-db
  [(re-frame/inject-cofx :get-browser-path)]
  (fn [{:keys [db browser-path]} _]
    (let [route (match-route routes browser-path)]
      (merge {:db (merge db/default-db {:route route})}
             (initial-dispatch route)))))

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
                                   :search-fail)
       :dispatch [:navigate :base {}]})))

(re-frame/reg-event-db
  :search-success
  (fn [db [_ result]]
    (assoc db :search-results (get-in result [:results
                                              :trackmatches
                                              :track] []))))

(re-frame/reg-event-fx
  :related-search
  (fn [{:keys [db]} [evt-type mbid]]
    {:http-xhrio (mk-get-request (mk-related-uri mbid)
                                 :related-success
                                 :related-fail)
     :dispatch [:navigate :related {:mbid mbid}]}))

(re-frame/reg-event-db
  :related-success
  (fn [db [_ result]]
    (assoc db :related-tracks (get-in result [:similartracks :track] []))))

(re-frame/reg-event-fx
  :navigate
  (fn [{:keys [db]} [evt-type route-name params]]
    (let [path (->> params
                    seq
                    flatten
                    (apply (partial path-for routes route-name)))]
    {:db (assoc db :route {:handler route-name :route-params params})
     :browser-history-push path})))

(re-frame/reg-fx
  :browser-history-push
  (fn [path]
    (-> js/window
        .-history
        (.pushState {} "" path))))

(re-frame/reg-cofx
  :get-browser-path
  (fn [cofx _]
    (assoc cofx :browser-path
      (-> js/window
          .-location
          .-pathname))))
