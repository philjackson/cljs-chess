(ns chess-test.prod
  (:require [chess-test.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
