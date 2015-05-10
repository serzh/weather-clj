(ns ^:figwheel-always weather-clj.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [goog.string :as string]
            [cljs-time.local :as local]
            [cljs-time.format :as tf]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! put! chan]]))

(enable-console-print!)

(defonce data (reagent/atom {:location nil
                             :date (tf/unparse (tf/formatter "yyyy-MM-dd")
                                               (local/local-now))}))

(defonce cities [{:value "tampere" :label "Tampere" :coords {:lat 61.498056 :long 23.760833}}
                 {:value "london"  :label "London" :coords {:lat 51.507222 :long -0.1275}}
                 {:value "durham"  :label "Durham NC" :coords {:lat 36.04 :long -78.87}}])

(defn city-select [cities data]
  [:div.form-group
   [:label {:for "location"} "Location"]
   [:select {:id        "location"
             :class     "form-control"
             :on-change #(swap! data assoc :location (-> % .-target .-value))
             :value     (:location @data)}
    (map (fn [{val :value lbl :label}]
           [:option {:value val} lbl])
         cities)]])

(defn date-select [data]
  [:div.form-group
   [:label {:for "date"} "Date"]
   [:input {:id        "date"
            :class     "form-control"
            :type      :date
            :value     (:date @data)
            :on-change #(swap! data assoc :date (->> % .-target .-value))}]])

(defn set-conditions! [data]
  (go
    (let [{:keys [location date]} @data
          date-str (->> date
                        (tf/parse (tf/formatter "yyyy-MM-dd"))
                        (tf/unparse (tf/formatter "yyyyMMdd")))
          conditions (:body (<! (http/get (string/format "/conditions/%s/%s"
                                                         location
                                                         date-str))))]
      (swap! data assoc :conditions conditions))))

(defn fetch-button [data]
  [:button {:type :button
            :class "btn btn-primary"
            :on-click #(set-conditions! data)}
   "Fetch"])

(defn conditions [data]
  (let [{:keys [temp wind-kph pressure]} (:conditions @data)]
    [:div.row
     [:h2 "Conditions"]
     [:dl.dl-horizontal
      [:dt "Temperature"]
      [:dd temp " ℃"]

      [:dt "Wind speed"]
      [:dd wind-kph " kph"]

      [:dt "Pressure"]
      [:dd pressure " Gpa"]]]))

(defn deg->rad [deg]
  (* deg (/ Math/PI 180)))

(defn distance [{f1 :lat l1 :long} {f2 :lat l2 :long}]
  (let [earth-r 6371
        f1 (deg->rad f1)
        l1 (deg->rad l1)
        f2 (deg->rad f2)
        l2 (deg->rad l2)]
    (* 2 earth-r
       (Math/asin
         (Math/sqrt
           (+ (Math/pow (Math/sin (/ (- f2 f1) 2)) 2)
              (* (Math/cos f1)
                 (Math/cos f2)
                 (Math/pow (Math/sin (/ (- l2 l1) 2)) 2))))))))

(defn get-current-position-chan []
  (let [ch (chan)]
    (.getCurrentPosition js/navigator.geolocation
                         #(let [cs (.-coords %)]
                           (put! ch {:lat   (.-latitude cs)
                                     :long (.-longitude cs)})))
    ch))

(defn set-closest-to-user-conditions! [data cities]
  (go
    (let [user-coords (<! (get-current-position-chan))
          closest (->> cities
                       (map #(assoc % :dist (distance user-coords (:coords %))))
                       (apply min-key :dist))]
      (swap! data assoc :location (:value closest))
      (set-conditions! data))))

(defn weather-component []
  (fn []
    (set-closest-to-user-conditions! data cities)
    [:form
     [city-select cities data]
     [date-select data]
     [fetch-button data]
     [conditions data]]))

(reagent/render-component [:div [weather-component]]
                          (.getElementById js/document "conditions"))
