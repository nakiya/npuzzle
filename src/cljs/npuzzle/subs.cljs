(ns npuzzle.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::name
  (fn [db]
    (:name db)))

(re-frame/reg-sub
  ::dimension
  (fn [db]
    (:dim db)))

(re-frame/reg-sub
  ::puzzle
  (fn [db]
    (:puzzle db)))

(re-frame/reg-sub
  ::solved?
  (fn [db]
    (:solved? db)))

(re-frame/reg-sub
  ::solution
  (fn [db]
    (:solution db)))
