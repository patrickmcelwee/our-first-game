(ns our-first-game.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
    (assoc (texture "clojure.svg.png")
           :x 50 :y 50 :width 100 :height 100
           :angle 45 :origin-x 0 :origin-y 0
           ))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))
  :on-key-down
  (fn [screen entities]
    (cond
      (= (:key screen) (key-code :dpad-up))
      (println "up")
      (= (:key screen) (key-code :dpad-down))
      (println "down")
      (= (:key screen) (key-code :dpad-right))
      (println "right")
      (= (:key screen) (key-code :dpad-left))
      (println "left")
      )
    )
  )

(defgame our-first-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
