(ns npuzzle.db)

(defn manhattan-distance [pos1 pos2 dim]
  (let [row1 (quot pos1 dim)
        col1 (mod pos1 dim)
        row2 (quot pos2 dim)
        col2 (mod pos2 dim)]
    (+ (Math/abs (- row1 row2))
       (Math/abs (- col1 col2)))))

(defn neighbor? [pos1 pos2 dim]
  (= (manhattan-distance pos1 pos2 dim) 1))

;(defn get-neighbors [puzzle dim]
;  (when puzzle
;    (let [zp (.indexOf puzzle 0)
;          zr (quot zp dim)
;          zc (mod zp dim)]
;      (map
;        (fn [[row col]]
;          (let [np (+ (* row dim) col)
;                num (get puzzle np)]
;            (-> puzzle
;                (assoc np 0)
;                (assoc zp num))))
;        (filter #(not (nil? %))
;                (map
;                  (fn [dr dc]
;                    (when (and (< -1 (+ zr dr) dim) (< -1 (+ zc dc) dim))
;                      [(+ zr dr) (+ zc dc)]))
;                  [-1 0 1 0]
;                  [0 1 0 -1]))))))
;
;(defn can-move? [puzzle num dim]
;  (if (zero? num)
;    false
;    (neighbor? (.indexOf puzzle 0) (.indexOf puzzle num) dim)))

(defn try-move [puzzle num dim]
  (if (zero? num)
    puzzle
    (let [zero-pos (.indexOf puzzle 0)
          num-pos (.indexOf puzzle num)]
      (if (neighbor? zero-pos num-pos dim)
        (-> puzzle
            (assoc zero-pos num)
            (assoc num-pos 0))
        puzzle))))

(defn- inversions [puzzle]
  (->> puzzle
       (filter #(not (zero? %)))
       (map-indexed (fn [idx num] [idx (dec num)]))
       (#(for [i % j %] [i j]))
       (map (fn [[[num1 pos1] [num2 pos2]]]
              (if (and (< num2 num1) (< pos1 pos2))
                1
                0)))
       (reduce +)))

(defn solved? [puzzle]
  (= puzzle (conj (vec (range 1 (count puzzle))) 0)))

(defn solvable? [puzzle dim]
  "Find number of inversions and then check different cases for even and odd dimensions: https://www.cs.bham.ac.uk/~mdr/teaching/modules04/java2/TilesSolvability.html"
  (let [inversions (inversions puzzle)]
    (if (odd? dim)
      (even? inversions)
      (let [zero-row (quot (.indexOf puzzle 0) dim)]
        (if
          (or (and (even? zero-row) (odd? inversions))
              (and (odd? zero-row) (even? inversions)))
          true
          false)))))

(defn random-puzzle [dim]
  (into [] (shuffle (range (* dim dim)))))

(defn valid-random-puzzle [dim]
  (->> (repeatedly #(random-puzzle dim))
       (filter #(solvable? % dim))
       (filter #(not (solved? %)))
       first))

;(defn- assoc-multi [ordered-map key value]
;  (update ordered-map key (fnil conj #{}) value))
;
;(defn- dissoc-multi [ordered-map key value]
;  (let [result (update ordered-map key disj value)]
;    (cond-> result
;            (empty? (get result key)) (dissoc key))))
;
;(defn- first-multi [ordered-map]
;  (let [[key entries] (first ordered-map)
;        value (first entries)]
;    (if key
;      [key value]
;      [nil nil])))
;
;(defn heuristic [puzzle dim]
;  (->> puzzle
;       (filter #(not (zero? %)))
;       (map-indexed (fn [idx num] (manhattan-distance idx (dec num) dim)))
;       (reduce +)))

(defn- create-path [node path]
  (if node
    (create-path (nth node 1) (conj path (first node)))
    path))

;(defn- make-mm [vals]
;  (into (sorted-map)
;        (into []
;              (map #(vec [(first %) (set (rest %))]) vals))))

(defn- reconstruct-path [came-from current]
  (if-let [parent (get came-from current)]
    (conj (reconstruct-path came-from parent) current)
    [current]))

;(defn- get-moving-tiles [path]
;  (let [p1 (vec path)
;        p2 (into [(first p1)] (pop p1))]
;    (rest (map #(get %2 (.indexOf %1 0)) p2 p1))))

;(defn- A* [open-sorted closed-set g-scores f-scores came-from dim]
;  (when (not (empty? open-sorted))
;    (let [[current _] (peek open-sorted)]
;      (if (solved? current)
;        (get-moving-tiles (reconstruct-path came-from current))
;        (let [closed-set (conj closed-set current)
;              open-sorted (pop open-sorted)
;              neighbors (filter #(nil? (get closed-set %))
;                                (get-neighbors current dim))
;              current-g (get g-scores current)]
;          (let [tentative-g-scores (map #(+ 1 current-g) neighbors)
;                G-scores (map #(if-let [g (get g-scores %)]
;                                 g 1000000)
;                              neighbors)
;                neighbors (->> (map #(hash-map :gt %1 :g %2 :puzzle %3)
;                                    tentative-g-scores G-scores neighbors)
;                               (filter #(< (:gt %) (:g %))))
;                came-from (reduce #(assoc %1 (:puzzle %2) current) came-from neighbors)
;                g-scores (reduce #(assoc %1 (:puzzle %2) (:gt %2)) g-scores neighbors)
;                f-scores (reduce #(assoc %1 (:puzzle %2) (+ (:gt %2) (heuristic (:puzzle %2) dim)))
;                                 f-scores neighbors)
;                open-sorted (reduce #(assoc %1 (:puzzle %2) (+ (:gt %2) (heuristic (:puzzle %2) dim)))
;                                    open-sorted neighbors)]
;            (recur open-sorted closed-set g-scores f-scores came-from dim)))))))
;
;(defn solve [start dim]
;  (let [closed-set #{}
;        h (heuristic start dim)
;        g-scores {start 0}
;        f-scores {start h}
;        open-sorted (into (pmap/priority-map) [[start h]])
;        came-from {start nil}]
;    (A* open-sorted closed-set g-scores f-scores came-from dim)))

(defn create-db [dim]
  {:dim dim
   :puzzle (valid-random-puzzle dim)
   :solved? false
   :solution nil})

