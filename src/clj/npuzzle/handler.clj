(ns npuzzle.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [npuzzle.layout :refer [error-page]]
            [npuzzle.routes.home :refer [home-routes]]
            [npuzzle.routes.services :refer [service-routes]]
            [compojure.route :as route]
            [npuzzle.env :refer [defaults]]
            [mount.core :as mount]
            [npuzzle.middleware :as middleware]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(mount/defstate app
  :start
  (middleware/wrap-base
    (routes
      (-> #'home-routes
          (wrap-routes middleware/wrap-csrf)
          (wrap-routes middleware/wrap-formats))
      #'service-routes
      (route/not-found
        (:body
          (error-page {:status 404
                       :title "page not found"}))))))
