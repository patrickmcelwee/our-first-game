(ns our-first-game.core.desktop-launcher
  (:require [our-first-game.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. our-first-game "our-first-game" 800 600)
  (Keyboard/enableRepeatEvents true))
