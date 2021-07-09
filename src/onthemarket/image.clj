(ns onthemarket.image
  (:require [clojure.core.matrix :as m]
            [the-flood.core :as flood]
            [clojure.string :as str]))

(defn clear [image]
  (m/fill image \O))

(defn create [height width]
  (clear (m/new-matrix height width)))

(defn dot->fill [image x y c]
  (m/mset image x y c))

(defn inclusive-range [x y]
  (conj (into [] (range x y)) y))

(defn vertical-fill [image x y1 y2 c]
  (m/set-selection image (inclusive-range y1 y2) x c))

(defn horizontal-fill [image x1 x2 y c]
  (m/set-selection image y (inclusive-range x1 x2) c))

(defn flood-fill [image x y c]
  (flood/flood-fill (m/coerce image) [x y] c nil))

(defn render [image]
  (str/join \newline (map #(apply str %) image)))


(defn radius-fill-inner [offset colour]
  {:offset offset :colour colour})

(defn radius-fill-inner-inner [x y]
  (fn [{:keys [offset colour]} image]
    (vertical-fill image (- x offset) ())))
    ;(vertical-fill image)
    ;(horizontal-fill)
    ;(horizontal-fill)

(defn radius-fill [image x y colours]
  ;; Fill the base dot not sure if this is required yet
  (let [image (dot->fill image x y (first colours))
        colours (map-indexed radius-fill-inner colours)
        image (reduce (radius-fill-inner-inner x y) image colours)]
       image))



(defn paint! [image]
  (println (render image))
  (flush))
