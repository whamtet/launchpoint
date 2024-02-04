(ns simpleui.launchpoint.web.views.home
    (:require
      [simpleui.core :as simpleui :refer [defcomponent]]
      [simpleui.launchpoint.web.views.login :as login]
      [simpleui.launchpoint.web.htmx :refer [page-htmx]]))

(defn ui-routes [base-path]
  (simpleui/make-routes
   base-path
   (fn [req]
     (page-htmx
      {:css ["/output.css"]}
      (login/login req)))))
