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

(defn result-item [get-artist item]
  [:div.panel.col-md-4 {:key (:mbid item) :on-click #(re-frame/dispatch [:related-search (:mbid item)])}
    [:div.panel-heading (:name item)]
    [:div.panel-body [:img.img-responsive {:src (:img item)}]]
    [:div.panel-footer [:strong (get-artist item)]]])

(defn search-results []
  (let [results (re-frame/subscribe [:search-results])]
    (fn []
      [:div (map (partial result-item #(:artist %)) @results)])))

(defn related-tracks []
  (let [related-tracks (re-frame/subscribe [:related-tracks])]
    (fn []
      [:div (map (partial result-item #(get-in % [:artist :name])) @related-tracks)])))

(defn handle-form-submit [evt]
  (.preventDefault evt)
  (re-frame/dispatch [:new-search]))

(defn main-panel []
  (let [name (re-frame/subscribe [:name])
        route (re-frame/subscribe [:route])]
    (fn []
      [:div.container-fluid
        [:form {:role "form" :on-submit handle-form-submit}
          [:input.form-control {:type "text"
                                :value @name
                                :on-change handle-name-change}]]
        [search-history]
        [(case (:handler @route)
           :related related-tracks
           search-results)]])))
