(ns simpleui.launchpoint.web.controllers.user)

(defn get-user [{:keys [query-fn session]}]
  (query-fn :get-user session))
