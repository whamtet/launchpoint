(ns simpleui.launchpoint.web.controllers.profile)

(defn upsert [{:keys [session query-fn]}]
  (query-fn :upsert-cv (assoc session :cv "fuck" :id 2)))
