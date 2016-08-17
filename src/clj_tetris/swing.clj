(ns clj-tetris.swing
  (:gen-class)
  (:import [javax.swing AbstractAction KeyStroke JPanel JFrame])
  (:import [java.awt Color Rectangle])
  (:require [clj-tetris.core :as tcore :refer :all]))

(def gray (Color. 48 99 99))
(def silver (Color. 210 255 255))
(def lighter-gray (Color. 165 185 185))
(def bright-gray (Color. 228 242 242))

(def main-frame (JFrame. "Tetris"))

(def block-size 5)
(def block-margin 5)
(def block-size-plus-margin (+ block-size block-margin))

(defn create-rect
  [view [pos-x, pos-y]]
  (Rectangle. (* pos-x block-size-plus-margin)
              (* (- (last (:grid-size view)) pos-y) block-size-plus-margin)
              block-size
              block-size))

(defn draw-empty-grid
  [graphics view]
  (.setColor graphics lighter-gray)
  (for [x (range (- (first (:grid-size view)) 1)) y (range (- (last (:grid-size view)) 2))]
    (.draw graphics (create-rect view [x y]))))

(defn draw-blocks
  [graphics view blocks]
  (if (not (empty? blocks))
    (.fill graphics (create-rect view (:position (first blocks))))
    (draw-blocks graphics view (rest blocks))))

(defn draw-old-blocks
  [graphics view]
  (.setColor graphics bright-gray)
  (draw-blocks graphics view (:old-blocks view)))

(defn draw-current-piece
  [graphics view]
  (.setColor graphics silver)
  (draw-blocks graphics view (:current-piece view)))

(defn onPaint [graphics]
  (let [view (@tcore/game-view)]
    (.setColor graphics silver)
    (draw-empty-grid graphics view)
    (draw-old-blocks graphics view)
    (draw-current-piece graphics view)))

(def main-panel
  (proxy [JPanel] []
    (paint [graphics]
      (do
        (let [panel-width (.getWidth (.getSize this)) panel-height (.getHeight (.getSize this))]
          (.setColor graphics gray)
          (.fillRect graphics 0 0 panel-width panel-height)
          (onPaint graphics))))))

(def tetris-space-action
  (proxy [AbstractAction] []
    (actionPerformed [event]
      (.repaint main-panel))))

(def tetris-down-action
  (proxy [AbstractAction] []
    (actionPerformed [event]
      (.repaint main-panel))))

(def tetris-up-action
  (proxy [AbstractAction] []
    (actionPerformed [event]
      (.repaint main-panel))))

(def tetris-left-action
  (proxy [AbstractAction] []
    (actionPerformed [event]
      (tcore/move-left)
      (.repaint main-panel))))

(def tetris-right-action
  (proxy [AbstractAction] []
    (actionPerformed [event]
      (tcore/move-right)
      (.repaint main-panel))))

(defn -main
  [& args]

  ;Example key binding
  (.put (.getInputMap main-panel) (KeyStroke/getKeyStroke "SPACE") "tetrisSpaceAction")
  (.put (.getInputMap main-panel) (KeyStroke/getKeyStroke "DOWN") "tetrisDownAction")
  (.put (.getInputMap main-panel) (KeyStroke/getKeyStroke "UP") "tetrisUpAction")
  (.put (.getInputMap main-panel) (KeyStroke/getKeyStroke "LEFT") "tetrisLeftAction")
  (.put (.getInputMap main-panel) (KeyStroke/getKeyStroke "RIGHT") "tetrisRightAction")

  (.put (.getActionMap main-panel) "tetrisSpaceAction" tetris-space-action)
  (.put (.getActionMap main-panel) "tetrisDownAction" tetris-down-action)
  (.put (.getActionMap main-panel) "tetrisUpAction" tetris-up-action)
  (.put (.getActionMap main-panel) "tetrisLeftAction" tetris-left-action)
  (.put (.getActionMap main-panel) "tetrisRightAction" tetris-right-action)

  ;Setup panel properties
  (.setContentPane main-frame main-panel)

  (.setSize main-panel 200 200)
  (.setFocusable main-panel true)

  (.setSize main-frame 200 200)
  (.setVisible main-frame true))



