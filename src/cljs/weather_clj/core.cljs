(ns ^:figwheel-always weather-clj.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [goog.string :as string]
            [cljs-time.local :as local]
            [cljs-time.format :as tf]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(enable-console-print!)

(defonce data (reagent/atom {:location "tampere"
                             :date (tf/unparse (tf/formatter "yyyy-MM-dd")
                                               (local/local-now))}))

(defn city-select [cities data]
  [:select {:on-change #(swap! data assoc :location (-> % .-target .-value))
            :value (:location @data)}
   (map (fn [[val lbl]]
          [:option {:value val} lbl])
        cities)])

(defn date-select [data]
  [:input {:type      :date
           :value     (:date @data)
           :on-change #(swap! data assoc :date (-> % .-target .-value))}])

(defn fetch-button [data]
  [:button {:on-click (fn [e]
                        (let [{:keys [location date]} @data
                              date-str (->> date
                                           (tf/parse (tf/formatter "yyyy-MM-dd"))
                                           (tf/unparse (tf/formatter "yyyyMMdd")))]
                          (go
                            (let [conditions (:body (<! (http/get (string/format "/conditions/%s/%s"
                                                                                 location
                                                                                 date-str))))]
                              (swap! data assoc :conditions conditions)))))}
   "Fetch"])

(defn conditions [data]
  [:div (pr-str (:conditions @data))])

(defn weather-component []
  (let [cities [["tampere" "Tampere"]
                ["london" "London"]
                ["durham" "Durham NC"]]]
    (fn []
      [:div
       [city-select cities data]
       [date-select data]
       [fetch-button data]
       [conditions data]])))

(reagent/render-component [:div [weather-component]]
                          (.-body js/document))
