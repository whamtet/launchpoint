(ns simpleui.launchpoint.web.controllers.item-order
    (:require
      [simpleui.launchpoint.web.controllers.store :as store]))

(defn basket-count [{:keys [query-fn session]}]
  (:items (query-fn :basket-count session)))

(defn- update-inventory [k {:keys [query-fn session]} inventory_id]
  (query-fn k (assoc session :inventory_id inventory_id)))

(defmacro defupdate [s]
  `(defn ~s [req# inventory_id#]
    (some->
      (update-inventory ~(keyword s) req# inventory_id#)
      first
     :quantity)))

(defupdate add-order)
(defupdate inc-order)
(defupdate dec-order)
(defupdate del-order)

(defn my-order [{:keys [query-fn session]}]
  (->>
   (query-fn :my-order session)
   (map #(-> % :inventory_id store/items-raw (merge %)))
   not-empty))
