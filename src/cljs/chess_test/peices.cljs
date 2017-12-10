(ns chess-test.peices
  (:require [chess-test.log :refer [log]]))

(defn parse-piece [piece]
  (let [[colour rank] (vec piece)]
    [(if (= colour "b") :black :white)
     (case rank
       "p" :pawn
       "n" :knight
       "k" :king
       "q" :queen
       "b" :bishop
       "r" :rooke)]))

(defn on-row? [row]
  (fn [_ _ position]
    (= (first position) row)))

(defn unoccupied? [board-data _ position]
  (not (get @board-data position)))

(defn occupied-by-op? [board-data colour position]
  (when-let [op (get @board-data position)]
    (let [[op-colour op-rank] (parse-piece op)]
      (not= op-colour colour))))

(defn occupied-by-op-or-unoccupied? [board-data colour position]
  (or (unoccupied? board-data colour position)
      (occupied-by-op? board-data colour position)))

(def king [[:forward occupied-by-op-or-unoccupied?]
           [:backward occupied-by-op-or-unoccupied?]
           [:left occupied-by-op-or-unoccupied?]
           [:right occupied-by-op-or-unoccupied?]])

(def pawn [[:forward unoccupied?]
           [(on-row? 2) :forward unoccupied? :forward unoccupied?]
           [:forward :right occupied-by-op?]
           [:forward :left occupied-by-op?]])
