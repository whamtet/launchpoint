(ns simpleui.launchpoint.web.views.profile
    (:require
      [simpleui.core :as simpleui]
      [simpleui.launchpoint.web.controllers.profile :as profile]
      [simpleui.launchpoint.web.htmx :refer [page-htmx defcomponent]]
      [simpleui.response :as response]))

(defcomponent ^:endpoint profile [req command]
  (case command
        "create" (prn (profile/upsert req))
        [:div {:hx-post "profile:create"}
         "hi"]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (if (-> req :session :id)
       (let [req (assoc req :query-fn query-fn)]
         (page-htmx
          {:css ["/output.css"] :hyperscript? true}
          (profile req)))
       (response/redirect "/")))))
