(ns weather-clj.web
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [ring.middleware
             [resource :refer [wrap-resource]]
             [keyword-params :refer [wrap-keyword-params]]
             [webjars :refer [wrap-webjars]]]
            [ring.util.response :as res]
            [compojure.core :as route :refer [GET]]
            [schema.core :as s]
            [weather-clj.wunderground :as wunderground]
            [cheshire.core :as json]
            [weather-clj.storage :as storage]))

(defn get-weather [{{:keys [city date]} :params
                    {storage :storage wun :wunderground} :web}]
  (let [result (or (storage/fetch storage city date)
                   (->> (wunderground/get-weather wun city date)
                        (json/generate-string)
                        (storage/save! storage city date)))]
    (-> result
        (res/response)
        (res/content-type "application/json"))))

(route/defroutes routes
  (GET "/conditions/:city/:date" req (get-weather req))
  (GET "/" req (res/resource-response "public/index.html")))

(defn wrap-with-web [h c]
  (fn [req]
    (h (assoc req :web c))))

(defn handler [c]
  (-> routes
      (wrap-with-web c)
      (wrap-keyword-params)
      (wrap-webjars "/assets")
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