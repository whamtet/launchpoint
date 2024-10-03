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
     (let [req (assoc req :query-fn query-fn)
           logged-in? (-> req :session :id)]
       (page-htmx
        {:css ["/output.css"]
         :hyperscript? true
         :google? (not logged-in?)}
        (if logged-in?
          (dashboard/dashboard req)
          (login/login req)))))))
