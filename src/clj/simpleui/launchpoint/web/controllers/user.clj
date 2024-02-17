(ns simpleui.launchpoint.web.controllers.user)

(defn get-user [{:keys [query-fn session]}]
  (query-fn :get-user session))

(defn update-names [{:keys [query-fn session]} first_name last_name]
  (query-fn :update-names
            (assoc session
                   :first_name first_name
                   :last_name last_name)))

(defn search-users [{:keys [query-fn]} q]
  (query-fn :search-users {:q (str "%" q "%")}))
