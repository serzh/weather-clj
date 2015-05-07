(ns weather-clj.system
  (:require [com.stuartsierra.component :as component]
            [weather-clj.web :as web]
            [weather-clj.wunderground :as wunderground]
            [weather-clj.storage :as storage]))

(defn new-system
  [{:keys [web-host web-port
           db-host db-port db-name db-user db-pass
           wunderground-api-key]}]
  (component/system-map
    :web (component/using
           (web/new-web {:host web-host
                        :port (Integer/valueOf web-port)})
           [:wunderground :storage])
    :wunderground (wunderground/new-wunderground {:api-key wunderground-api-key})
    :storage (storage/new-storage {:host db-host
                                   :port (Integer/valueOf db-port)
                                   :dbname db-name
                                   :user db-user
                                   :pass db-pass})))