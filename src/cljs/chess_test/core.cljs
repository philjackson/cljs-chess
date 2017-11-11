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
(def board-data (atom {"1a" "br" "1b" "bn" "1c" "bb" "1d" "bq" "1e" "bk" "1f" "bb" "1g" "bn" "1h" "br"
                       "2a" "bp" "2b" "bp" "2c" "bp" "2d" "bp" "2e" "bp" "2f" "bp" "2g" "bp" "2h" "bp"
                       "7a" "wp" "7b" "wp" "7c" "wp" "7d" "wp" "7e" "wp" "7f" "wp" "7g" "wp" "7h" "wp"
                       "8a" "wr" "8b" "wn" "8c" "wb" "8d" "wk" "8e" "wq" "8f" "wb" "8g" "wn" "8h" "wr"}))

;; -------------------------
;; Views

(defn square-colour [r c]
  (if (= 0 (rem r 2))
    (if (= 0 (rem c 2)) "tan" "brown")
    (if (= 0 (rem c 2)) "brown" "tan")))

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

(defn update-pos [cur-pos nxt-pos piece]
  (let [copy @board-data]
    (reset! board-data (-> copy
                           (dissoc cur-pos)
                           (assoc nxt-pos piece)))))

(defn get-pos [&moves]
  )

(defn allowed-moves [position piece]
  (let [[row col] (vec position)
        [colour rank] (parse-piece piece)]
    (case rank
      :pawn (cond-> []
              (= row "2") (conj (str 3 col) (str 4 col)))
     [])))

(def rows [1 2 3 4 5 6 7 8])
(def cols [\a \b \c \d \e \f \g \h])

(defn board []
  [:div.board
   (doall
    (for[r rows]
      (doall
       (for [c cols]
         (let [nm (str r c)
               piece (get @board-data nm)]
           ^{:key nm} [:div.square {:id nm
                                    :class (square-colour r (.charCodeAt c 0))
                                    :on-click (fn []
                                                (reset! currently-selected-square (if piece nm nil))
                                                (reset! allowed-moves-data (if piece (allowed-moves nm piece) [])))}
                       (let [allowed-move? (some #{nm} @allowed-moves-data)]
                         [:div.inner-square {:class (when (= nm @currently-selected-square) "is-selected")}
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
