(ns clojure-ass.spec
  (:require [cljs.spec :as s]
            [re-frame.core :as re-frame]))

(s/def ::name string?)
(s/def ::term string?)
(s/def ::history (s/* ::term))
(s/def ::result (s/keys :req-un [::name ::artist]))
(s/def ::search-results (s/* ::result))
(s/def ::related-tracks (s/* ::result))
(s/def ::db (s/keys :req-un [::name ::history ::search-results ::related-tracks]))

(defn- check-and-throw
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor (re-frame/after (partial check-and-throw ::db)))
