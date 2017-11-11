(ns chess-test.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [chess-test.core :as rc]))

(deftest test-parse-position
  (is (= [1 2] (rc/parse-position "12"))))

(deftest test-get-pos
  (is (= [3 1] (rc/get-pos "11" :black :forward :forward)))
  (is (= [2 2] (rc/get-pos "11" :black :forward :right)))
  (is (= [1 1] (rc/get-pos "11" :black :forward :forward :right :backward :backward :left))))
