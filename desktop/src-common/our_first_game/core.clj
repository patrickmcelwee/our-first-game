(ns our-first-game.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(def base-speed 8)

(defn flip [entity]
  (let [direction (:direction entity)
        texture-direction (:texture-direction entity)]
    (when-not (= direction texture-direction)
      (texture! entity :flip true false))
    (assoc entity :texture-direction direction)
    )
 )

(defn walk [entity]
  (if (:walking entity)
    (case (:direction entity)
      :right (flip (assoc entity :x (+ (:x entity) base-speed)))
      :left (flip (assoc entity :x (- (:x entity) base-speed))))
    entity))

(defn move-and-face [entity direction]
  (-> entity (flip direction) (move direction)))

(defn go-home [entity]
  (assoc entity :x 50 :y 50))

(def max-jump-speed 12.0)

(defn start-jump [entity]
  (assoc entity :y-velocity max-jump-speed))

(defn gravity [entity]
  (if (:static entity)
    entity
    (if (and (<= (:y entity) 0) (<= (:y-velocity entity) 0))
      (assoc entity :y 0 :y-velocity 0)
      (assoc entity
             :y (+ (:y entity) (:y-velocity entity))
             :y-velocity (- (:y-velocity entity) 0.5)))))

(defn- find-by-name [entities a-name]
  (let [entity (first (filter #(= (:name %) a-name) entities))]
    [entity (.indexOf entities entity)]))

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
            :y-velocity 0
            :texture-direction :right
            :direction :right)
     (assoc (shape :filled
                   :set-color (color :green)
                   :rect 0 0 300 20)
            :name "block" :x 30 :y 35 :static true)
     ]
    )
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen (map #(-> % walk gravity) entities)))
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
               (assoc hero :walking true :direction :right)
               (= (:key screen) (key-code :dpad-left))
               (assoc hero :walking true :direction :left)
               (= (:key screen) (key-code :j))
               (start-jump hero)
               :else hero)
             ))
    )
  :on-key-up
  (fn [screen entities]
    (let [[hero index] (find-by-name entities "hero")]
      (assoc entities index
             (cond
               (or (= (:key screen) (key-code :dpad-right))
                   (= (:key screen) (key-code :dpad-left)))
               (dissoc hero :walking)
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
