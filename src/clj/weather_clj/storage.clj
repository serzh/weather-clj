(ns weather-clj.storage
  (:require [com.stuartsierra.component :as component]
            [schema.core :as s]))

(defrecord Storage [host port dbname user pass spec]
  component/Lifecycle
  (start [c]
    (assoc c :spec {:subprotocol "postgresql"
                    :subname (format "//%s:%d/%s" host port dbname)
                    :user user
                    :password pass}))
  (stop [c]
    (assoc c :spec nil)))

(def StorageSchema
  {:host s/Str
   :port s/Int
   :dbname s/Str
   :user s/Str
   :pass s/Str})

(defn new-storage [m]
  (s/validate StorageSchema m)
  (map->Storage m))

