(ns npuzzle.puzzle
  (:require [clojure.data.priority-map :refer [priority-map]]))

(defn manhattan-distance [pos1 pos2 dim]
  (let [row1 (quot pos1 dim)
        col1 (mod pos1 dim)
        row2 (quot pos2 dim)
        col2 (mod pos2 dim)]
    (+ (Math/abs (- row1 row2))
       (Math/abs (- col1 col2)))))

(defn get-neighbors [puzzle dim]
  (when puzzle
    (let [zp (.indexOf puzzle 0)
          zr (quot zp dim)
          zc (mod zp dim)]
      (map
        (fn [[row col]]
           (let [np (+ (* row dim) col)
                 num (get puzzle np)]
             (-> puzzle
                 (assoc np 0)
                 (assoc zp num))))
        (filter #(not (nil? %))
          (map
            (fn [dr dc]
              (when (and (< -1 (+ zr dr) dim) (< -1 (+ zc dc) dim))
                [(+ zr dr) (+ zc dc)]))
            [-1 0 1 0]
            [0 1 0 -1]))))))

(defn solved? [puzzle]
  (= puzzle (conj (vec (range 1 (count puzzle))) 0)))

(defn heuristic [puzzle dim]
  (->> puzzle
       (filter #(not (zero? %)))
       (map-indexed (fn [idx num] (manhattan-distance idx (dec num) dim)))
       (reduce +)))

(defn- create-path [node path]
  (if node
    (create-path (nth node 1) (conj path (first node)))
    path))

(defn- reconstruct-path [came-from current]
  (if-let [parent (get came-from current)]
    (conj (reconstruct-path came-from parent) current)
    [current]))

(defn- get-moving-tiles [path]
  (let [p1 (vec path)
        p2 (into [(first p1)] (pop p1))]
    (rest (map #(get %2 (.indexOf %1 0)) p2 p1))))

(defn- A* [open-sorted closed-set g-scores f-scores came-from dim iterations]
  (do
    (when (= 0 (mod iterations 1000))
      (println (first open-sorted)))
    (when (not (empty? open-sorted))
      (let [[current _] (peek open-sorted)]
        (if (solved? current)
          [(get-moving-tiles (reconstruct-path came-from current)) iterations]
          (let [closed-set (conj closed-set current)
                open-sorted (pop open-sorted)
                neighbors (filter #(nil? (get closed-set %))
                                  (get-neighbors current dim))
                current-g (get g-scores current)]
            (let [tentative-g-scores (replicate (count neighbors) (+ 1 current-g))
                  G-scores (map #(if-let [g (get g-scores %)]
                                   g 1000000)
                                neighbors)
                  neighbors (->> (map #(hash-map :gt %1 :g %2 :puzzle %3)
                                      tentative-g-scores G-scores neighbors)
                                 (filter #(< (:gt %) (:g %))))
                  came-from (reduce #(assoc %1 (:puzzle %2) current) came-from neighbors)
                  g-scores (reduce #(assoc %1 (:puzzle %2) (:gt %2)) g-scores neighbors)
                  f-scores (reduce #(assoc %1 (:puzzle %2) (+ (:gt %2) (heuristic (:puzzle %2) dim)))
                                   f-scores neighbors)
                  open-sorted (reduce #(assoc %1 (:puzzle %2) (+ (:gt %2) (heuristic (:puzzle %2) dim)))
                                      open-sorted neighbors)]
              (recur open-sorted closed-set g-scores f-scores came-from dim (inc iterations)))))))))

(defn solve [start dim]
  (let [closed-set #{}
        h (heuristic start dim)
        g-scores {start 0}
        f-scores {start h}
        open-sorted (into (priority-map) [[start h]])
        came-from {start nil}]
    (let [[result iterations] (A* open-sorted closed-set g-scores f-scores came-from dim 0)]
      (println "Solving took " iterations " iterations")
      result)))


