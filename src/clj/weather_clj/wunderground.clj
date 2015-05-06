(ns weather-clj.wunderground
  (:require [org.httpkit.client :as http]
            [com.stuartsierra.component :as component]
            [schema.core :as s]
            [clj-xpath.core :refer [$x:text $x]]))

(defrecord Wunderground [api-key]
  component/Lifecycle
  (start [c] c)
  (stop [c] c))

(defn parse-data [data]
  {:temp     (Integer/valueOf ($x:text "/response/history/dailysummary/summary/meantempm" data))
   :wind-kph (Integer/valueOf ($x:text "/response/history/dailysummary/summary/meanwindspdm" data))
   :pressure (Double/valueOf ($x:text "/response/history/dailysummary/summary/meanpressurem" data))})

(defn get-weather [wun city date]
  (let [{api-key :api-key} wun
        city-full ({"tampere" "Finland/Tampere"
                    "london" "England/London"
                    "durham" "NC/Durham"} city)
        uri (format "http://api.wunderground.com/api/%s/history_%s/q/%s.xml"
                    api-key date city-full)
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
  (get-weather (Wunderground. "6ef205eeb64534c6") "Finland/Tampere" "20150506"))