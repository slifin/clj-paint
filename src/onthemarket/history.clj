(ns onthemarket.history)

(def history (atom []))

(defn latest []
  (last @history))

(defn delete! []
  (reset! history []))

(defn append [val]
  (swap! history conj val))
