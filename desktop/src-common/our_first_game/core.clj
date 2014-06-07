(ns our-first-game.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(def speed 30)

(defn move [entity direction]
  (case direction
    :down (assoc entity :y (- (:y entity) speed))
    :up (assoc entity :y (+ (:y entity) speed))
    :right (assoc entity :x (+ (:x entity) speed) :direction :right)
    :left (assoc entity :x (- (:x entity) speed) :direction :left)
    nil))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (assoc (texture "game_guy1.png")
           :x 50 :y 50 :width 93 :height 215 :direction :right
           ))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))
  :on-resize
  (fn [screen entities]
    (height! screen 600))
  :on-key-down
  (fn [screen entities]
    (let [hero (first entities)]
      (cond
        (= (:key screen) (key-code :h))
        (assoc hero :x 50 :y 50)
        (= (:key screen) (key-code :dpad-up))
        (move hero :up)
        (= (:key screen) (key-code :dpad-down))
        (move hero :down)
        (= (:key screen) (key-code :dpad-right))
        (do
          (when-not (= (:direction hero) :right)
            (texture! hero :flip true false)
            )
          (move hero :right)
          )
        (= (:key screen) (key-code :dpad-left))
        (do
          (when-not (= (:direction hero) :left)
            (texture! hero :flip true false)
            )
          (move hero :left)
          )
      ))
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
