(ns npuzzle.events
  (:require [re-frame.core :as re-frame]
            [npuzzle.db :as db]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]))

(re-frame/reg-event-db
  ::initialize-db
  (fn  [_ _]
    (db/create-db 3)))

(re-frame/reg-event-db
  ::move-piece
  (fn [db [_ num]]
    (if (not= 0 num)
      (-> db
          (assoc :solved? (db/solved? (:puzzle db)))
          (assoc :puzzle (db/try-move (:puzzle db) num (:dim db))))
      db)))

(re-frame/reg-event-db
  ::change-dimension
  (fn [db [_ dim]]
    (db/create-db dim)))

(re-frame/reg-event-db
  ::shuffle
  (fn [db [_ _]]
    (db/create-db (:dim db))))

;(re-frame/reg-event-db
;  ::solve
;  (fn [db [_ _]]
;    (assoc db :solution (db/solve (:puzzle db) (:dim db)))))
;
;(re-frame/reg-event-db
;  ::solve
;  (fn [db [_ _]]
;    (assoc db :solution (db/solve (:puzzle db) (:dim db)))))

(re-frame/reg-event-db
  ::next-step
  (fn [db [_ _]]
    (let [num (first (:solution db))]
      (do
        (re-frame/dispatch [::move-piece num])
        (assoc db :solution (rest (:solution db)))))))

(re-frame/reg-event-db
  ::solve-success
  (fn [db [_ result]]
    (assoc db :solution result)))

(re-frame/reg-event-db
  ::solve-failure
  (fn [db [_ result]]
    (assoc db :solution (str "Error : " result))))

(re-frame/reg-event-fx
  ::solve
  (fn [{:keys [db]}]
    {:http-xhrio {:method          :post
                  :uri             "api/solve"
                  :params          {:puzzle (:puzzle db) :dim (:dim db)}
                  :timeout         300000
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::solve-success]
                  :on-failure      [::solve-failure]}}))
