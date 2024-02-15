(ns simpleui.launchpoint.web.controllers.store
    (:require
      [clojure.data.json :as json]
      [clojure.java.io :as io]
      [simpleui.launchpoint.util :as util]))

(defn- set-rating [{:keys [rate count]}]
  {:count count
   :rate (* rate count)})
(defn- prep-item [m]
  (-> m
      (update :id dec)
      (update :rating set-rating)))

(def items-raw
  (-> "store.json"
      io/resource
      slurp
      (json/read-str :key-fn keyword)
      (->> (mapv prep-item))))

(defn- add-rating [items {:keys [id rating]}]
  (-> items
      (update-in [id :rating :rate] + rating)
      (update-in [id :rating :count] inc)))

(defn- add-count [items {:keys [id count]}]
  (assoc-in items [id :count] count))

(defn items [{:keys [query-fn]}]
  (as-> items-raw items
        (reduce add-rating items (query-fn :ratings-all {}))
        (reduce add-count items (query-fn :inventory-all {}))))
