(ns chess-test.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(defn log [message]
  (.log js/console message)
  message)

(def currently-selected-square (atom "r1c1"))

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

(defn board []
  [:div.board
   (doall
    (for [r (range 1 9)]
      (doall
       (for [c (range 1 9)]
         (let [nm (str "r" r "c" c)]
           ^{:key nm} [:div.square {:id nm :class (square-colour r c)}
                       (when-let [figure (get @board-data nm)]
                         [:div.inner-square {:class (when (= nm @currently-selected-square)
                                                      "is-selected")}
                          [:img {:src (str "img/pieces/maya/" figure ".svg")}]])])))))])

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
