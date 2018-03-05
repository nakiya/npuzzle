(ns npuzzle.app
  (:require [npuzzle.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
