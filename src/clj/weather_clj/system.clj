(ns weather-clj.system
  (:require [com.stuartsierra.component :as component]
            [weather-clj.web :as web]))

(defn new-system
  [{:keys [web-host web-port]}]
  (component/system-map
    :web (web/new-web {:host web-host
                       :port (Integer/valueOf web-port)})))