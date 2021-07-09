(ns onthemarket.command
  (:require [cli-matic.core :refer [run-cmd*]]
            [cli-matic.utils :as util]
            [onthemarket.history :as history]
            [onthemarket.image :as image]
            [clojure.string :as str]
            [clojure.spec.alpha :as s]))

(def colours (set (map str "ABCDEFGHIJKLMNOPQRSTUVWXYZ")))

(defn create [{:keys [width height]}]
  (image/create width height))

(defn clear [_]
  (image/clear (history/latest)))

(defn dot->fill [{:keys [x y c]}]
  (image/dot->fill (history/latest) (dec x) (dec y) (.charAt c 0)))

(defn vertical-fill [{:keys [x y1 y2 c]}]
  (image/vertical-fill (history/latest) (dec x) (dec y1) (dec y2) (.charAt c 0)))

(defn horizontal-fill [{:keys [x1 x2 y c]}]
  (image/horizontal-fill (history/latest) (dec x1) (dec x2) (dec y) (.charAt c 0)))

(defn flood-fill [{:keys [x y c]}]
  (image/flood-fill (history/latest) (dec x) (dec y) (.charAt c 0)))

(defn paint! [_]
  (image/paint! (history/latest)))

(defn exit! [_]
  (System/exit 0))

(s/def :clj-paint/boundary (s/and int? #(< % 250)))

(def configuration
  {:app      {:command     "clj-paint"
              :description "Graphical Editor"
              :version     "0.0.1"}
   :commands [{:command     "init"
               :short       "I"
               :description "Creates a new image with given dimensions"
               :examples    ["I 5 6"]
               :opts        [{:option  "width"
                              :type    :int
                              :short   1
                              :default 5
                              :spec :clj-paint/boundary}
                             {:option  "height"
                              :short   0
                              :type    :int
                              :default 6
                              :spec :clj-paint/boundary}]
               :runs        (comp history/append create)}
              {:command     "clear"
               :short       "C"
               :description "Clears the image to white (O)"
               :examples    ["C"]
               :runs        (comp history/append clear)}
              {:command     "Paint dot to given colour"
               :short       "L"
               :description "Fills given dot with given colour"
               :examples    ["L 1 1 A"]
               :runs        (comp history/append dot->fill)
               :opts        [{:option "x"
                              :type   :int
                              :short  1}
                             {:option "y"
                              :type   :int
                              :short  0}
                             {:option "c"
                              :type   colours
                              :short  2}]}
              {:command     "Fill vertical slice to given colour"
               :short       "V"
               :description "Fills a vertical segment with given colour"
               :examples    ["V 1 1 3 A"]
               :runs        (comp history/append vertical-fill)
               :opts        [{:option "x"
                              :type   :int
                              :short  0}
                             {:option "y1"
                              :type   :int
                              :short  1}
                             {:option "y2"
                              :type   :int
                              :short  2}
                             {:option "c"
                              :type   colours
                              :short  3}]}
              {:command     "Fill horizontal slice to given colour"
               :short       "H"
               :description "Fills a horizontal slice with given colour"
               :examples    ["H 1 2 3 A"]
               :runs        (comp history/append horizontal-fill)
               :opts        [{:option "y"
                              :type   :int
                              :short  2}
                             {:option "x1"
                              :type   :int
                              :short  0}
                             {:option "x2"
                              :type   :int
                              :short  1}
                             {:option "c"
                              :type   colours
                              :short  3}]}
              {:command     "Flood fill with given colour"
               :short       "F"
               :description "Fills neighbours who share the colour at given x and y with given colour"
               :examples    ["F 1 1 D"]
               :runs        (comp history/append flood-fill)
               :opts        [{:option "x"
                              :type   :int
                              :short  0}
                             {:option "y"
                              :type   :int
                              :short  1}
                             {:option "c"
                              :type   colours
                              :short  2}]}
              {:command     "Show the current image"
               :short       "S"
               :description "Shows the current image in the terminal"
               :examples    ["S"]
               :runs        paint!}
              {:command     "Exit the running program"
               :short       "X"
               :description "Closes the application"
               :runs        exit!}]})

(defn welcome []
  (print (str (char 27) "[2J"))
  (print (str (char 27) "[;H"))
  (println "Welcome to clj-paint \uD83D\uDC4B")
  (println "For help please read doc/user-guide.pdf"))

(defn execute [command]
  (run-cmd* configuration (str/split command #" ")))

(defn output! [command]
  (let [{:keys [help stderr subcmd status] :as rtn} (execute command)]
    (if (seq stderr)
      (println (util/asString ["** ERROR **" stderr]))
      (cond
        (= :HELP-GLOBAL help)
        (println (util/asString ((get-in configuration [:app :global-help]) configuration)))
        (= :HELP-SUBCMD help)
        (println (util/asString ((get-in configuration [:app :subcmd-help]) configuration subcmd)))
        (= command "clear") (welcome)
        :else (println status)))
    rtn))

(comment
  (execute "I 3 2")
  (execute "C")
  (execute "L 1 1 A")
  (execute "V 2 1 2 C")
  (execute "H 1 1 1 A")
  (execute "F 1 1 D")
  (execute "S")
  (execute "X")
  (history/latest)
  (history/delete!))
