(ns weather-clj.system
  (:require [com.stuartsierra.component :as component]
            [weather-clj.web :as web]
            [weather-clj.wunderground :as wunderground]))

(defn new-system
  [{:keys [web-host web-port
           api-key]}]
  (component/system-map
    :web (web/new-web {:host web-host
                       :port (Integer/valueOf web-port)})
    :wunderground (wunderground/new-wunderground {:api-key api-key})))