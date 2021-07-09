(ns onthemarket.clj-paint
  (:require [onthemarket.command :as command])
  (:gen-class))

(defn -main [& _]
  (command/welcome)
  (while true (command/output! (read-line))))
