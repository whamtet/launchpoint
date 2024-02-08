(ns simpleui.launchpoint.web.controllers.company
    (:require
      [clojure.java.io :as io]))

(def companies
  (->> "companies.edn"
       io/resource
       slurp
       read-string
       (map (fn [[company img]]
              [(.toLowerCase company) [company img]]))
       (into {})))

(defn search [q]
  (->> companies
       (keep (fn [[company info]]
               (when (.contains company q) info)))
       (keep 5)))
