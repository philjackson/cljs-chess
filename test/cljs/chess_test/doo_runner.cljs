(ns chess-test.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [chess-test.core-test]))

(doo-tests 'chess-test.core-test)
