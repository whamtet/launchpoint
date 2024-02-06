(ns simpleui.launchpoint.web.views.home
    (:require
      [simpleui.core :as simpleui]
      [simpleui.launchpoint.web.views.dashboard :as dashboard]
      [simpleui.launchpoint.web.views.login :as login]
      [simpleui.launchpoint.web.htmx :refer [page-htmx]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (let [req (assoc req :query-fn query-fn)]
       (page-htmx
        {:css ["/output.css"]}
        (if (-> req :session :id)
          (dashboard/dashboard req)
          (login/login req)))))))
