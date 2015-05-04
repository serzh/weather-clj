(ns ^:figwheel-always weather-clj.core
  (:require [reagent.core :as reagent]
            [cljs-time.local :as local]
            [cljs-time.format :as tf]))

(enable-console-print!)

(defn city-select [cities data]
  [:select {:on-change #(swap! data assoc :location (-> % .-target .-value))}
   (map (fn [[val lbl]]
          [:option {:value val} lbl])
        cities)])

(defn date-select [data]
  [:input {:type      :date
           :value     (:date @data)
           :on-change #(swap! data assoc :date (-> % .-target .-value))}])

(defn fetch-button [data]
  [:button {:on-click (fn [e] (prn @data))}
   "Fetch"])

(defn weather-component []
  (let [cities [["tampere" "Tampere"]
                ["london" "London"]
                ["durham" "Durham NC"]]
        data (reagent/atom {:location "tampere"
                            :date (tf/unparse (tf/formatter "yyyy-MM-dd")
                                              (local/local-now))})]"Fetch"
    (fn []
      [:div
       [city-select cities data]
       [date-select data]
       [fetch-button data]])))

(reagent/render-component [:div [weather-component]]
                          (.-body js/document))
