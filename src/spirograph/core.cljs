(ns spirograph.core
  (:require [quil.core :as q :include-macros true]
     [quil.middleware :as m]))

;(enable-console-print!)

(def setting {:base-r 150
              :gear-r 80
              :point-r 50
              :base-angle-update-rate 0.01
              :gear-angle-update-rate -0.01
              :base-color [255 0 0]
              :point-color [0 0 255]})

(defn update-setting
  [e]
  ;(println "update-setting called")
  (let [base-r (.-value (.getElementById js/document "base-r"))
        gear-r (.-value (.getElementById js/document "gear-r"))
        point-r (.-value (.getElementById js/document "point-r"))
        base-angle-update-rate (.-value (.getElementById js/document "base-angle-update-rate"))
        gear-angle-update-rate (.-value (.getElementById js/document "gear-angle-update-rate"))
        base-color-r (.-value (.getElementById js/document "base-color-r"))
        base-color-g (.-value (.getElementById js/document "base-color-g"))
        base-color-b (.-value (.getElementById js/document "base-color-b"))
        point-color-r (.-value (.getElementById js/document "point-color-r"))
        point-color-g (.-value (.getElementById js/document "point-color-g"))
        point-color-b (.-value (.getElementById js/document "point-color-b"))]
    (q/with-sketch (q/get-sketch-by-id "spirograph")
                   (swap! (q/state-atom) assoc-in [:setting] {:base-r (js/parseInt base-r)
                                                              :gear-r (js/parseInt gear-r)
                                                              :point-r (js/parseInt point-r)
                                                              :base-angle-update-rate (js/parseFloat base-angle-update-rate)
                                                              :gear-angle-update-rate (js/parseFloat gear-angle-update-rate)
                                                              :base-color [(js/parseInt base-color-r)
                                                                           (js/parseInt base-color-g)
                                                                           (js/parseInt base-color-b)]
                                                              :point-color [(js/parseInt point-color-r)
                                                                            (js/parseInt point-color-g)
                                                                            (js/parseInt point-color-b)]}))))

(defn clear-canvas
  [e]
  ;(println "clear-canvas called")
  (q/with-sketch (q/get-sketch-by-id "spirograph")
                 (q/background 240)))

(defn point-by-angle
  [center-x center-y r angle]
  (let [x (* r (q/cos angle))
        y (* r (q/sin angle))]
    {:x (+ center-x x) :y (+ center-y y) :angle angle}))

(defn setup []
  (q/frame-rate 300)
  (q/color-mode :rgb)
  (q/background 240)
  (.addEventListener (.getElementById js/document "update-btn") "click" update-setting)
  (.addEventListener (.getElementById js/document "clear-btn") "click" clear-canvas)
  {:color 0
   :angle 0
   :gear-angle 0
   :setting setting})

(defn update-state [state]
  ;(println state)
  (let [angle (+ (:angle state) (:base-angle-update-rate (:setting state)))
        gear-angle (+ (:gear-angle state) (:gear-angle-update-rate (:setting state)))
        center-x (/ (q/width) 2)
        center-y (/ (q/height) 2)
        base-point (point-by-angle center-x
                                   center-y
                                   (:base-r (:setting state)) 
                                   angle)
        gear-center-x (Math/abs (- (:x base-point) (* (q/cos angle) (:gear-r (:setting state)))))
        gear-center-y (Math/abs (- (:y base-point) (* (q/sin angle) (:gear-r (:setting state)))))
        gear-point (point-by-angle gear-center-x
                                   gear-center-y
                                   (:point-r (:setting state)) 
                                   gear-angle)]
    {:color (mod (+ (:color state) 0.7) 255)
     :base-point base-point
     :gear-point gear-point
     :angle angle
     :gear-angle gear-angle
     :setting (:setting state)}))

(defn draw-state [state]
  (apply q/stroke (:base-color (:setting state)))
  (q/point (:x (:base-point state)) (:y (:base-point state)))
  (apply q/stroke (:point-color (:setting state)))
  (q/point (:x (:gear-point state)) (:y (:gear-point state))))

(q/defsketch spirograph
             :host "spirograph"
             :size [400 400]
             :setup setup
             :update update-state
             :draw draw-state
             :middleware [m/fun-mode])
