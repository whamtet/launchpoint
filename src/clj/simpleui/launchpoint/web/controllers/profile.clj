(ns simpleui.launchpoint.web.controllers.profile)

(defn- upsert [{:keys [session query-fn]} cv]
  (query-fn :upsert-cv (assoc session :cv (pr-str cv))))

(defn get-cv [{:keys [session query-fn]}]
  (some-> (query-fn :get-cv session)
          :cv
          read-string))

(defn- update-cv [req f & args]
  (->> (apply f (get-cv req) args)
       (upsert req)))

(defn update-description [req description]
  (update-cv req assoc :description description))

