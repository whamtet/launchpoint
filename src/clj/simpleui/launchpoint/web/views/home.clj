(ns simpleui.launchpoint.web.views.home
    (:require
      [simpleui.core :as simpleui :refer [defcomponent]]
      [simpleui.launchpoint.web.views.login :as login]
      [simpleui.launchpoint.web.htmx :refer [page-htmx]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (->> (assoc req :query-fn query-fn)
          login/login
          (page-htmx {:css ["/output.css"]})))))
