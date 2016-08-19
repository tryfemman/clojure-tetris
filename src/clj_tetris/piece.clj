(ns clj-tetris.piece
  (:require [clj-tetris.piece-kind :refer :all]))

(def standard-rotation-theta (/ (- Math/PI) 2.0))

(defrecord Block [position piece-kind])

(defrecord Piece [position kind local-points])

(defn piece-current-blocks
  [piece]
  (let [piece-posisition (:position piece)
        pos-x (first piece-posisition)
        pos-y (last piece-posisition)]
    (map
      (fn
        [[local-pos-x local-pos-y]]
        (Block.
          [(int (Math/floor (+ local-pos-x pos-x)))
           (int (Math/floor (+ local-pos-y pos-y)))]
          (:kind piece)))
      (:local-points piece))))

(defn move-piece
  [piece delta]
  (println (str "Piece position " (:position piece)))
  (let [piece-position (:position piece)
        old-pos-x (first piece-position)
        old-pos-y (last piece-position)
        new-position [(+ old-pos-x (first delta))
                      (+ old-pos-y (last delta))]]
    (Piece. new-position (:kind piece) (:local-points piece))))


(defn rotate-piece
  [piece]
  (let [theta standard-rotation-theta
        cos-theta (Math/cos theta)
        sin-theta (Math/sin theta)
        current-position (:position piece)
        local-points (:local-points piece)
        piece-kind (:kind piece)]
    (Piece. current-position piece-kind
            (mapv
              (comp
                (fn [[x y]]
                  [(* (Math/round (* (double x) 2.0)) 0.5)
                   (* (Math/round (* (double y) 2.0)) 0.5)])
                (fn [[x y]]
                  [(- (* cos-theta x) (* sin-theta y))
                   (+ (* sin-theta x) (* cos-theta y))]))
              local-points))))


(defn create-piece
  [position piece-kind]
  (cond
    (= t-kind piece-kind) (Piece. position piece-kind [[-1.0 0.0] [0.0 0.0] [1.0 0.0] [0.0 1.0]])
    :else (throw (IllegalStateException. (str "Tried to create a piece of kind" (type piece-kind))))))
