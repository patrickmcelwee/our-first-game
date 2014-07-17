(ns our-first-game.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(def base-speed 8)

(defn flip [entity direction]
 (when-not (= (:direction entity) direction)
   (texture! entity :flip true false))
  entity
 )

(defn move
  ([entity direction] (move entity direction base-speed))
  ([entity direction speed]
   (case direction
     :down (assoc entity :y (- (:y entity) speed))
     :up (assoc entity :y (+ (:y entity) speed))
     :right (assoc entity :x (+ (:x entity) speed) :direction :right)
     :left (assoc entity :x (- (:x entity) speed) :direction :left)
     nil)))

(defn move-and-face [entity direction]
  (-> entity (flip direction) (move direction)))

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

(defn- find-by-name [entities a-name]
  (let [entity (first (filter #(= (:name %) a-name) entities))]
    [entity (.indexOf entities entity)]
    )
  )

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (sound "everglade.mp3" :loop)
    [
     (assoc (texture "game_guy1.png")
            :name "hero"
            :x 50
            :y 50
            :width 93
            :height 215
            :direction :right)
     (assoc (shape :filled
                   :set-color (color :green)
                   :rect 0 0 300 20)
            :name "block" :x 30 :y 35)
     ]
    )
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen (map #(jump %) entities)))
  :on-resize
  (fn [screen entities]
    (height! screen 600))
  :on-key-down
  (fn [screen entities]
    (let [[hero index] (find-by-name entities "hero")]
      (assoc entities index
             (cond
               (= (:key screen) (key-code :h))
               (go-home hero)
               (= (:key screen) (key-code :dpad-right))
               (move-and-face hero :right)
               (= (:key screen) (key-code :dpad-left))
               (move-and-face hero :left)
               (= (:key screen) (key-code :j))
               (start-jump hero)
               :else hero)
             ))
    )
  :on-touch-down
  (fn [screen entities]
    (let [position (input->screen screen (:input-x screen) (:input-y screen))
          [hero index] (find-by-name entities "hero")]
      (assoc entities index
             (cond
               (> (:x position) (* (width screen) (/ 2 3)))
               (move-and-face hero :right)
               (< (:x position) (/ (width screen) 3))
               (move-and-face hero :left)
               :else hero))
      )
    )
  )

(defgame our-first-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))

(defscreen blank-screen
  :on-render
  (fn [screen entities]
    (clear!)))

(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                            (catch Exception e
                              (.printStackTrace e)
                              (set-screen! our-first-game blank-screen)))))
