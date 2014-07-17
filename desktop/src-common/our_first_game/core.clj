(ns our-first-game.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(def speed 8)

(defn flip [entity direction]
 (when-not (= (:direction entity) direction)
   (texture! entity :flip true false)))

(defn move [entity direction]
  (case direction
    :down (assoc entity :y (- (:y entity) speed))
    :up (assoc entity :y (+ (:y entity) speed))
    :right (assoc entity :x (+ (:x entity) speed) :direction :right)
    :left (assoc entity :x (- (:x entity) speed) :direction :left)
    nil))

(defn move-and-face [entity direction]
 (do (flip entity direction)
     (move entity direction)))

(defn go-home [entity]
  (assoc entity :x 50 :y 50))

(defn start-jump [entity]
  (if (:jump entity)
    entity
    (assoc entity :jump 1))
    )

(defn jump [entity]
  (if (:jump entity)
    (do
        (if (< (:jump entity) 30)
          (if (< (:jump entity) 16)
            (move (assoc entity :jump (inc (:jump entity))) :up)
            (move (assoc entity :jump (inc (:jump entity))) :down))
          (move (dissoc entity :jump) :down)
          )
        )
    entity
    ))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (sound "everglade.mp3" :loop)
    (assoc (texture "game_guy1.png")
           :x 50 :y 50 :width 93 :height 215 :direction :right
           ))
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen (map #(jump %) entities)))
  :on-resize
  (fn [screen entities]
    (height! screen 600))
  :on-key-down
  (fn [screen entities]
    (let [hero (first entities) ]
      (cond
        (= (:key screen) (key-code :h))          (go-home hero)
        (= (:key screen) (key-code :dpad-right)) (move-and-face hero :right)
        (= (:key screen) (key-code :dpad-left))  (move-and-face hero :left)
        (= (:key screen) (key-code :j))          (start-jump hero)
      ))
    )
  :on-touch-down
  (fn [screen entities]
    (let [position (input->screen screen (:input-x screen) (:input-y screen))]
      (cond
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
