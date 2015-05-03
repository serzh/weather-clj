 (ns dev
   (:require [ns-tracker.core :refer (ns-tracker)]
             [weather-clj.system :as s]
             [com.stuartsierra.component :as component]
             [environ.core :as environ]
             [figwheel-sidecar.repl :as figrepl]))

(defonce system nil)

(defn init []
  (alter-var-root #'system (constantly (s/new-system environ/env))))

(defn start []
  (alter-var-root #'system component/start))

(defn go []
  (init)
  (start))

(defn stop []
  (when system
    (alter-var-root #'system component/stop)))

(def ^:private modified-ns
  (ns-tracker ["src"]))

(defn reload-ns []
  (doseq [ns-sym (modified-ns)]
    (require ns-sym :reload)))

(defn reset []
  (stop)
  (reload-ns)
  (go))

(def cljs-repl figrepl/cljs-repl)