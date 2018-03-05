(ns npuzzle.views
  (:require [re-frame.core :as re-frame]
            [npuzzle.subs :as subs]))

(defn puzzle-box []
  (let [dim (re-frame/subscribe [::subs/dimension])
        puzzle (re-frame/subscribe [::subs/puzzle])
        solved? (re-frame/subscribe [::subs/solved?])
        box-width "600px"
        box-height "600px"
        item-percent (str (/ 99 @dim) "%")
        item-margin (str (/ 1 @dim) "%")]
    (into [:div.puzzlebox.m-1 {:style {:width box-width :height box-height}}]
          (map
            (fn [num]
              [:div.puzzlesub
               (into {:style {:width item-percent :height item-percent :margin-right item-margin :margin-bottom item-margin}
                      :class (if (= num 0) "puzzle-piece zero-piece" "puzzle-piece")}
                  (when (not @solved?)
                     [[:on-click #(re-frame/dispatch [:npuzzle.events/move-piece num])]]))
               [:div.puzzle-piece
                [:h2 num]]])
            @puzzle))))

(defn config-panel []
  (let [dim (re-frame/subscribe [::subs/dimension])
        solution (re-frame/subscribe [::subs/solution])]
      [:div.row
        [:div.col-sm-12.m-1
          [:label {:for "dim_select"} "Select dimension"]
          [:select#dim_select.form-control.mt-10
            {:value @dim
             :placeholder "Select dimension"
             :on-change #(re-frame/dispatch [:npuzzle.events/change-dimension
                                             (int (-> % .-target .-value))])}
            [:option {:value 3} "3x3"]
            [:option {:value 4} "4x4"]
            [:option {:value 5} "5x5"]
            [:option {:value 6} "6x6"]]]
        [:div.col-sm-12.m-1
          [:button.btn.btn-primary {:type :button
                                    :on-click #(re-frame/dispatch [:npuzzle.events/shuffle])}
           "Shuffle"]]
       [:div.col-sm-12.m-1
          [:label (str "Solution: " @solution)]
        [:div.col-sm-12.m-1
          [:button.btn.btn-primary {:type :button
                                    :on-click #(re-frame/dispatch [:npuzzle.events/next-step])}
            ">"]]]
       [:div.col-sm-12.m-1
          [:button.btn.btn-primary {:type :button
                                    :on-click #(re-frame/dispatch [:npuzzle.events/solve])}
           "Solve"]]]))


(defn victory-notification []
  (let [solved? (re-frame/subscribe [::subs/solved?])]
    (when @solved?
      (js/alert "Congraats!"))))

(defn main-panel []
  [:div.container
   (puzzle-box)
   (config-panel)
   (victory-notification)])
