(ns weather-clj.web
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [ring.middleware
             [resource :refer [wrap-resource]]
             [keyword-params :refer [wrap-keyword-params]]]
            [ring.util.response :as res]
            [compojure.core :as route :refer [GET]]
            [schema.core :as s]
            [weather-clj.wunderground :as wunderground]
            [cheshire.core :as json]))

(defn get-weather [{{:keys [city date]} :params}]
  (res/content-type {:body    (json/generate-string {:city city :date date})
                     :status  200}
                    "application/json"))

(route/defroutes routes
  (GET "/conditions/:city/:date" req (get-weather req))
  (GET "/" req (res/resource-response "public/index.html")))

(defn handler [c]
  (-> routes
      (wrap-keyword-params)
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