(ns our-first-game.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(def speed 10)

(defn move [entity direction]
  (case direction
    :down (assoc entity :y (- (:y entity) speed))
    :up (assoc entity :y (+ (:y entity) speed))
    :right (assoc entity :x (+ (:x entity) speed))
    :left (assoc entity :x (- (:x entity) speed))
    nil))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
    (assoc (texture "game_guy1.png")
           :x 50 :y 50 :width 186 :height 431
           ))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))
  :on-key-down
  (fn [screen entities]
    (cond
      (= (:key screen) (key-code :dpad-up))
      (move (first entities) :up)
      (= (:key screen) (key-code :dpad-down))
      (move (first entities) :down)
      (= (:key screen) (key-code :dpad-right))
      (move (first entities) :right)
      (= (:key screen) (key-code :dpad-left))
      (move (first entities) :left)
      )
    )
  :on-touch-down
  (fn [screen entities]
    (let [position (input->screen screen (:input-x screen) (:input-y screen))]
      (cond
        (> (:y position) (* (height screen) (/ 2 3)))
        (move (first entities) :up)
        (< (:y position) (/ (height screen) 3))
        (move (first entities) :down)
        (> (:x position) (* (width screen) (/ 2 3)))
        (move (first entities) :right)
        (< (:x position) (/ (width screen) 3))
        (move (first entities) :left)
        ))
    )
  )

(defgame our-first-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
