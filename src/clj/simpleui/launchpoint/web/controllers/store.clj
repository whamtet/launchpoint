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
      (assoc :count 100 :q (.toLowerCase (:title m)))
      (update :rating set-rating)))

(def ^:private items-raw
  (-> "store.json"
      io/resource
      slurp
      (json/read-str :key-fn keyword)
      (->> (mapv prep-item))))

(defn- add-rating [items {:keys [inventory_id rating]}]
  (-> items
      (update-in [inventory_id :rating :rate] + rating)
      (update-in [inventory_id :rating :count] inc)))

(defn- add-rating1 [item {:keys [rating]}]
  (assoc item :rating rating))

(defn- add-inventory [items {:keys [item_id price count]}]
  (-> items
      (update-in [item_id :price] #(or price %))
      (update-in [item_id :count] #(or count %))))

(defn items [{:keys [query-fn]}]
  (as-> items-raw items
        (reduce add-rating items (query-fn :ratings-all {}))
        (reduce add-inventory items (query-fn :inventory-all {}))))

(defn upsert-price [{:keys [query-fn]} item_id price]
  (query-fn :upsert-price {:item_id item_id :price price}))

(defn upsert-count [{:keys [query-fn]} item_id count]
  (query-fn :upsert-count {:item_id item_id :count count}))

(defn search-items [q]
  (filter #(.contains (:q %) q) items-raw))

(defn get-item [{:keys [path-params query-fn session] :as req}]
  (-> path-params
      :item-id
      Long/parseLong
      ((items req))
      (add-rating1 (query-fn :rating (merge path-params session)))))
