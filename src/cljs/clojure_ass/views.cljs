(ns clojure-ass.views
    (:require [re-frame.core :as re-frame]))

(defn handle-name-change [evt]
  (re-frame/dispatch [:name-change (-> evt
                                       .-target
                                       .-value)]))

(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:div.container-fluid
        [:input.form-control {:type "text"
                              :value @name
                              :on-change handle-name-change}]])))
