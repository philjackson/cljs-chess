(ns chess-test.moves)

(defn on-row? [row]
  (fn [_ position]
    (= (first position) row)))

(defn unoccupied? [board-data position]
  (not (get @board-data position)))

(def pawn [[:forward unoccupied?]
           [(on-row? 2) :forward unoccupied? :forward unoccupied?]])
