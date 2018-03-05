(ns user
  (:require 
            [mount.core :as mount]
            [npuzzle.figwheel :refer [start-fw stop-fw cljs]]
            [npuzzle.core :refer [start-app]]))

(defn start []
  (mount/start-without #'npuzzle.core/repl-server))

(defn stop []
  (mount/stop-except #'npuzzle.core/repl-server))

(defn restart []
  (stop)
  (start))


