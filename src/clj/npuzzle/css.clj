(ns npuzzle.css
  (:require [garden.def :refer [defstyles]]))

(defstyles screen
  [:.puzzlebox {:display :flex
                :flex-flow "row wrap"
                :align-items :center
                :justify-content :center}]
  [:.puzzlesub {:display :flex
                :flex-direction :column
                :align-items :center
                :justify-content :center
                :background-color "#aaa"}]
  [:.zero-piece {:visibility :hidden}]
  [:.puzzle-piece {}])

