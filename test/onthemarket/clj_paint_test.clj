(ns onthemarket.clj-paint-test
  (:require [clojure.test :refer [deftest is testing]]
            [onthemarket.command :as command]
            [onthemarket.history :as history]
            [onthemarket.image :as image]
            [clojure.string :as str]))

(defn parse [example]
  (let [lines (str/split-lines example)
        io (group-by #(if (str/starts-with? % ">") :inputs :result) lines)]
    {:inputs  (map #(subs % 2) (:inputs io))
     :result (filter not-empty (:result io))}))

(defn examples []
  (as-> (slurp "resources/examples.txt") ?
        (str/split ? #"- - - -")
        (map parse ?)))

(deftest paint!-test
  (testing "Reads clj-paint commands from disk performs them, then compares the results against predictions"
    (doseq [{inputs :inputs result :result} (examples)]
      (doseq [input inputs] (command/execute input))
      (is (= (str/join "\n" result) (image/render (history/latest))))
      (history/delete!))))

