(ns simpleui.launchpoint.web.controllers.delete
    (:require
      [simpleui.launchpoint.tmp :as tmp]))

(defn delete-user [{:keys [query-fn path-params]}]
  (doseq [{:keys [order_id]} (query-fn :delete-orders path-params)]
    (tmp/rm-receipt order_id))
  (doseq [query [:delete-cv :delete-user]]
    (query-fn query path-params)))
