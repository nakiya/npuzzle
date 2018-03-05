(ns ^:figwheel-no-load npuzzle.app
  (:require [npuzzle.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
