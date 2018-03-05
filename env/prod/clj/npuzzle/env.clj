(ns npuzzle.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[npuzzle started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[npuzzle has shut down successfully]=-"))
   :middleware identity})
