(ns chess-test.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(defn log [message]
  (.log js/console message)
  message)

(def currently-selected-square (atom "r1c1"))

(def allowed-moves-data (atom []))

;; initial layout
(def board-data (atom {"r1c1" "br" "r1c2" "bn" "r1c3" "bb" "r1c4" "bq" "r1c5" "bk" "r1c6" "bb" "r1c7" "bn" "r1c8" "br"
                       "r2c1" "bp" "r2c2" "bp" "r2c3" "bp" "r2c4" "bp" "r2c5" "bp" "r2c6" "bp" "r2c7" "bp" "r2c8" "bp"
                       "r7c1" "wp" "r7c2" "wp" "r7c3" "wp" "r7c4" "wp" "r7c5" "wp" "r7c6" "wp" "r7c7" "wp" "r7c8" "wp"
                       "r8c1" "wr" "r8c2" "wn" "r8c3" "wb" "r8c4" "wk" "r8c5" "wq" "r8c6" "wb" "r8c7" "wn" "r8c8" "wr"}))

;; -------------------------
;; Views

(defn square-colour [r c]
  (if (= 0 (rem r 2))
    (if (= 0 (rem c 2))
      "brown"
      "tan")
    (if (= 0 (rem c 2))
      "tan"
      "brown")))

(defn parse-position
  "Given a position string such as 'r1c1', extract row and column."
  [pos]
  (let [[_ row col] (first (re-seq #"r([\d])c([\d])" pos))]
    [row col]))

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

(defn pos [row col] (str "r" row "c" col))

(defn update-pos [cur-pos nxt-pos piece]
  (let [copy @board-data]
    (reset! board-data (-> copy
                           (dissoc cur-pos)
                           (assoc nxt-pos piece)))))

(defn allowed-moves [position piece]
  (let [[row col] (parse-position position)
        [colour rank] (parse-piece piece)]
    (case rank
      :pawn (when (= row "2")
              [(pos 3 col) (pos 4 col)])
     [])))

(defn board []
  [:div.board
   (doall
    (for [r (range 1 9)]
      (doall
       (for [c (range 1 9)]
         (let [nm (str "r" r "c" c)]
           (let [piece (get @board-data nm)]
             ^{:key nm} [:div.square {:id nm
                                      :class (square-colour r c)
                                      :on-click (fn []
                                                  (reset! currently-selected-square (if piece nm nil))
                                                  (reset! allowed-moves-data (if piece
                                                                               (allowed-moves nm piece)
                                                                               [])))}
                         (let [allowed-move? (some #{nm} @allowed-moves-data)]
                           [:div.inner-square {:class (when (= nm @currently-selected-square) "is-selected")}
                            (cond
                              allowed-move? [:span.allowed-move "•"]
                              piece [:img {:src (str "img/pieces/maya/" piece ".svg")}])])]))))))])

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
