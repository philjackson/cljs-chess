(ns chess-test.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [chess-test.peices :as peices]
              [chess-test.log :refer [log]]))

(def currently-selected-square (atom "r1c1"))

(def allowed-moves-data (atom []))

;; initial layout
(def board-data (atom {[1 1] "br" [1 2] "bn" [1 3] "bb" [1 4] "bq" [1 5] "bk" [1 6] "bb" [1 7] "bn" [1 8] "br"
                       [2 1] "bp" [2 2] "bp" [2 3] "bp" [2 4] "bp" [3 5] "bp" [2 6] "bp" [2 7] "bp" [2 8] "bp"
                       [7 1] "wp" [3 2] "wp" [7 3] "wp" [7 4] "wp" [7 5] "wp" [7 6] "wp" [7 7] "wp" [7 8] "wp"
                       [8 1] "wr" [8 2] "wn" [8 3] "wb" [8 4] "wk" [8 5] "wq" [8 6] "wb" [8 7] "wn" [8 8] "wr"}))

;; -------------------------
;; Views

(defn square-colour [r c]
  (if (= 0 (rem r 2))
    (if (= 0 (rem c 2)) "tan" "brown")
    (if (= 0 (rem c 2)) "brown" "tan")))

(defn update-pos [cur-pos nxt-pos piece]
  (let [copy @board-data]
    (reset! board-data (-> copy
                           (dissoc cur-pos)
                           (assoc nxt-pos piece)))))

(defn get-pos [position colour move]
  (let [[row col] (vec position)]
    (cond
      (= move :left) [row (dec col)]
      (= move :right) [row (inc col)]
      (= move :backward) [(dec row) col]
      (= move :forward) [(inc row) col])))

(defn allowed-moves [position piece]
  (let [[colour rank] (peices/parse-piece piece)]
    (doall
     (for [actions (case rank
                     :pawn peices/pawn
                     :king peices/king)]
       (reduce (fn [this-pos move]
                 (cond
                   (keyword? move) (get-pos this-pos colour move)
                   (ifn? move) (if (move board-data colour this-pos) this-pos (reduced nil))))
               position
               actions)))))

(defn board []
  [:div.board
   (doall
    (for[r (range 1 9)]
      (doall
       (for [c (range 1 9)]
         (let [piece (get @board-data [r c])]
           ^{:key (str r c)} [:div.square {:id (str r c)
                                    :class (square-colour r c)
                                    :on-click (fn []
                                                (reset! currently-selected-square (if piece [r c] nil))
                                                (reset! allowed-moves-data (if piece (allowed-moves [r c] piece) [])))}
                       (let [allowed-move? (some #{[r c]} @allowed-moves-data)]
                         [:div.inner-square {:class (when (= [r c] @currently-selected-square) "is-selected")}
                          (cond
                            allowed-move? [:span.allowed-move "•"]
                            piece [:img {:src (str "img/pieces/maya/" piece ".svg")}])])])))))])

(defn home-page []
  [board])

(defn about-page []
  [:div [:h2 "About chess-test"]
   [:div [:a {:href "/"} "go to the home page"]]])

;; -------------------------
;; Routes

(def page (atom #'home-page))

(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/about" []
  (reset! page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
