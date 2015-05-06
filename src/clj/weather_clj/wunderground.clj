(ns weather-clj.wunderground
  (:require [org.httpkit.client :as http]
            [com.stuartsierra.component :as component]
            [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [schema.core :as s]
            [clojure.xml :as xml]
            [clojure.java.io :as io]
            [clj-xpath.core :refer [$x:text $x]]))

(defrecord Wunderground [api-key]
  component/Lifecycle
  (start [c] c)
  (stop [c] c))

(defn parse-data [data]
  {:temp     (Integer/valueOf ($x:text "/response/current_observation/temp_c" data))
   :wind-kph (Integer/valueOf ($x:text "/response/current_observation/wind_kph" data))
   :weather  ($x:text "/response/current_observation/weather" data)})

(defn get-weather [wun city]
  (let [{api-key :api-key} wun
        uri (format "http://api.wunderground.com/api/%s/conditions/q/%s.xml"
                    api-key city)
        res @(http/get uri)]
    (when-let [error (:error res)]
      (throw error))
    (-> res
        :body
        parse-data)))

(def WundergroundSchema
  {:api-key s/Str})

(defn new-wunderground [m]
  (s/validate WundergroundSchema m)
  (map->Wunderground m))

(comment
  (get-weather (Wunderground. "6ef205eeb64534c6") "Finland/Tampere"))