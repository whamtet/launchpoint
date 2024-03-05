(ns simpleui.launchpoint.web.controllers.item-order)

(defn basket-count [{:keys [query-fn session]}]
  (:items (query-fn :basket-count session)))

(defn add-order [{:keys [query-fn session]} inventory_id]
  (query-fn :add-order (assoc session :inventory_id inventory_id)))
