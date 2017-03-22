(ns clojure-ass.views
    (:require [re-frame.core :as re-frame]))

(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:div.container-fluid
        [:input.form-control {:type "text"}]])))
