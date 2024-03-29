(ns simpleui.launchpoint.web.controllers.company
    (:require
      [clojure.java.io :as io]))

(def companies (->> "companies.edn" io/resource slurp read-string))
(def companies-lower
  (->> companies
       (map (fn [[company img]]
              [(.toLowerCase company) [company img]]))
       (into {})))

(defn search [q]
  (let [q (.toLowerCase q)
        {starters true
         others false}
        (->> companies-lower
             (filter (fn [[company]] (.contains company q)))
             (group-by (fn [[company]] (.startsWith company q))))]
    (->> (concat starters others)
         (take 5)
         (map second)
         not-empty)))
