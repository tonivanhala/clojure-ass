(ns clojure-ass.views
    (:require [re-frame.core :as re-frame]))

(defn handle-name-change [evt]
  (re-frame/dispatch [:name-change (-> evt
                                       .-target
                                       .-value)]))

(defn history-item [item]
  [:li {:key item} item])

(defn search-history []
  (let [history (re-frame/subscribe [:history])]
    (fn []
      [:ul.list-inline (map history-item @history)])))

(defn result-item [item]
  [:div.panel.col-md-4 {:key (:mbid item) :on-click #(re-frame/dispatch [:related-search (:mbid item)])}
    [:div.panel-heading (:name item)]
    [:div.panel-body [:img.img-responsive {:src (:img item)}]]
    [:div.panel-footer [:strong (:artist item)]]])

(defn search-results []
  (let [results (re-frame/subscribe [:search-results])]
    [:div (map result-item @results)]))

(defn handle-form-submit [evt]
  (.preventDefault evt)
  (re-frame/dispatch [:new-search]))

(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:div.container-fluid
        [:form {:role "form" :on-submit handle-form-submit}
          [:input.form-control {:type "text"
                                :value @name
                                :on-change handle-name-change}]]
        [search-history]
        [search-results]])))
