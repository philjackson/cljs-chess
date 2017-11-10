(ns ^:figwheel-no-load chess-test.dev
  (:require
    [chess-test.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
