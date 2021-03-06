(ns clj-tetris.agent-test
  (:require [clojure.test :refer :all]
            [clj-tetris.core :refer :all]
            [clj-tetris.piece-kind :refer :all]
            [clj-tetris.agent :as tagent]
            [clj-tetris.view :as view])
  (:import (clj_tetris.block Block)))

(deftest test-correct-move-for-clearing-rows
  (testing "Test that the agent correctly does not rotate or move the current piece"
    (is
      (do
        (reset-view [(Block. [0 0] t-kind)
                     (Block. [1 0] t-kind)
                     (Block. [2 0] t-kind)
                     (Block. [3 0] t-kind)
                     (Block. [7 0] t-kind)
                     (Block. [8 0] t-kind)
                     (Block. [9 0] t-kind)]
                    [t-kind o-kind o-kind o-kind o-kind o-kind])
        (empty? (tagent/next-move))))))

(deftest test-penalty-for-heights
  (testing "Test the heights calculated at a specific point for a view are correct (current piece is taken into account)"
    (is
      (=
        -93.1
        (tagent/evaluate-view
          (reset-view [(Block. [0 0] t-kind)
                       (Block. [0 1] t-kind)
                       (Block. [0 2] t-kind)
                       (Block. [0 3] t-kind)
                       (Block. [0 4] t-kind)
                       (Block. [0 5] t-kind)
                       (Block. [0 6] t-kind)]
                      [o-kind o-kind o-kind o-kind]))))))

(deftest test-view-evaluation
  (testing "Test that a game-over view takes negative evaluation"
    (is
      (let [game-over-view (assoc (view/create-initial-view [] [1 1] [] [1 1]) :game-over true)
            normal-view (view/create-initial-view [] [1 1] [] [1 1])]
        (and
          (= (tagent/evaluate-view game-over-view) -1000.0)
          (= (tagent/evaluate-view normal-view) -0.4))))))

(deftest test-action-seqs-for-initial-view
  (testing "Test that an initial view with a t-kind current piece has 29 possible action sequences"
    (is (count (tagent/action-seqs (reset-view [] [t-kind t-kind t-kind]))) 29)))

(deftest should-be-able-to-find-max-height-from-positions
  (testing "Should be able to find the maximum height for a collection of positions on a specific X position"
    (is (= (tagent/get-max-height-from-positions [[1.0 1.0] [1.0 2.0] [1.0 2.0] [1.0 3.0]]) 4.0))))

(def test-blocks
  [(Block. [1.0 1.0] i-kind)
   (Block. [2.0 2.0] t-kind)
   (Block. [3.0 3.0] t-kind)
   (Block. [1.0 4.0] t-kind)])

(deftest test-group-blocks-by-x-pos
  (testing "Should be able to group block positions by their x position"
    (is (= (get (tagent/group-blocks-by-x-axis test-blocks) 1.0) [[1.0 1.0] [1.0 4.0]]))))

(deftest test-get-heights
  (testing "Should be able to find the maximum heights for a collection of positions on all X positions"
    (is (= (tagent/get-heights test-blocks 4) [0 5.0 3.0 4.0]))))

(deftest test-get-penalty
  (testing "Should be able to calculate the penalty for a given set of blocks"
    (is (= (tagent/get-gap-penalty test-blocks 4) 50.0))))
