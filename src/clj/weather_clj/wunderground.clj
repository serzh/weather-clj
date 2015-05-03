(ns weather-clj.wunderground
  (:require [org.httpkit.client :as http]
            [com.stuartsierra.component :as component]
            [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [schema.core :as s]))

(defrecord Wunderground [api-key]
  component/Lifecycle
  (start [c] c)
  (stop [c] c))

(defn parse-data [data]
  (let [res (json/parse-string data keyword)]
    (when-let [error (get-in res [:response :error])]
      (log/errorf "Error parsing data. Error: \"%s\".\nData: \"%s\""
                  (pr-str error) (pr-str data)))
    (-> res
        :current_observation
        (select-keys [:weather :temp_c :wind_kph]))))

(defn get-weather [wun city]
  (let [{api-key :api-key} wun
        uri (format "http://api.wunderground.com/api/%s/conditions/q/%s.json"
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