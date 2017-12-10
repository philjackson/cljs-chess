(ns chess-test.log)

(defn log [& messages]
  (doseq [message messages ] (.log js/console message))
  (last messages))
