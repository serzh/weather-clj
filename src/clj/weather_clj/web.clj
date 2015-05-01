(ns weather-clj.web
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [ring.middleware
             [resource :refer [wrap-resource]]]
            [compojure.core :as route :refer [GET]]
            [schema.core :as s]))

(route/defroutes routes
  (GET "/" req "OK"))

(defn handler [c]
  (-> routes
      (wrap-resource "public")))

(defrecord Web [host port srv]
  component/Lifecycle
  (start [c]
    (let [s (jetty/run-jetty (handler c) {:host host
                                          :port port
                                          :join? false})]
      (assoc c :srv s)))
  (stop [c]
    (when srv (.stop srv))
    (assoc c :srv nil)))

(def WebSchema
  {:host s/Str
   :port s/Int})

(defn new-web [m]
  (s/validate WebSchema m)
  (map->Web m))