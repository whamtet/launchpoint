(ns simpleui.launchpoint.web.controllers.delete
    (:require
      [simpleui.launchpoint.tmp :as tmp]
      [simpleui.launchpoint.web.controllers.login :as login]))

(defn- delete-user* [query-fn user-id]
  (doseq [{:keys [order_id]} (query-fn :delete-orders {:user-id user-id})]
    (tmp/rm-receipt order_id))
  (doseq [query [:delete-cv :delete-user]]
    (query-fn query {:user-id user-id})))

(defn delete-user [{:keys [query-fn path-params]}]
  (delete-user* query-fn (:user-id path-params)))

(defn delete-self [{:keys [query-fn session]}]
  (delete-user* query-fn (:id session))
  (login/logout session))
