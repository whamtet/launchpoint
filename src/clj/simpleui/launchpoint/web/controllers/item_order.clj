(ns simpleui.launchpoint.web.controllers.item-order
    (:require
      [simpleui.launchpoint.web.controllers.store :as store]))

(defn basket-count [{:keys [query-fn session]}]
  (:items (query-fn :basket-count session)))

(defn- update-inventory [k {:keys [query-fn session]} inventory_id]
  (query-fn k (assoc session :inventory_id inventory_id)))
(defn- update-order [k {:keys [query-fn session path-params]}]
  (query-fn k (merge session path-params)))

(defmacro defupdate [s]
  `(defn ~s [req# inventory_id#]
    (->
     (update-inventory ~(keyword s) req# inventory_id#)
     first
     :quantity)))

(defupdate add-order)
(defupdate inc-order)
(defupdate dec-order)
(defupdate del-order)

(defn my-order [{:keys [query-fn session] :as req}]
  (let [items (store/items req)]
    (->>
     (query-fn :my-order session)
     (map #(-> % :inventory_id items (merge %)))
     not-empty)))

(defn order1 [req]
  (->> req
       (update-order :order1)
       :description
       read-string
       (map (fn [{:keys [id price quantity]}]
              {:price price
               :quantity quantity
               :title (get-in store/items-raw [id :title])}))))

(defn order1-owner? [req]
  (assert (update-order :order1 req)))

(defn- subtotal [{:keys [order_id description]}]
  (->> description
       read-string
       (map #(* (:price %) (:quantity %)))
       (apply +)
       (hash-map :order-id order_id :subtotal)))
(defn order-subtotals [{:keys [query-fn session]}]
  (map subtotal
       (query-fn :complete-orders session)))

(defn complete-order [{:keys [query-fn session] :as req}]
  (when-let [order (my-order req)]
    (let [order-id
          (->> order
               (map #(select-keys % [:id :price :quantity]))
               pr-str
               (assoc session :description)
               (query-fn :finalize-order)
               first
               :order_id)]
      (query-fn :del-order-all session)
      order-id)))
