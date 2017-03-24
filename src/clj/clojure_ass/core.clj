(ns clojure-ass.core
  (:require [clojure.java.io :as io]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn- routes [requ]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (io/input-stream (io/resource "public/index.html"))})

(def http-handler
  (wrap-defaults routes site-defaults))
