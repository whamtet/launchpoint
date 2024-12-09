(ns simpleui.launchpoint.web.views.preferences
    (:require
      [simpleui.core :as simpleui]
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.controllers.delete :as delete]
      [simpleui.launchpoint.web.controllers.item-order :as item-order]
      [simpleui.launchpoint.web.controllers.user :as user]
      [simpleui.launchpoint.web.htmx :refer [page-htmx defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.dashboard :as dashboard]
      [simpleui.response :as response]))

(defcomponent ^:endpoint preferences [req]
  (if (simpleui/delete? req)
    (delete/delete-self req)
    (let [{:keys [first_name]} (user/get-user req)
          basket-count (item-order/basket-count req)]
      [:div.min-h-screen.p-1 {:_ "on click add .hidden to .drop"}
       [:a.absolute.top-3.left-3 {:href "/"}
        [:img.w-24 {:src "/logo.svg"}]]
       (dashboard/main-dropdown basket-count first_name)
       [:div {:class "mt-16 w-2/3 mx-auto"}
        [:div#modal]
        [:span {:class "cursor-pointer"
                :hx-delete "preferences"
                :hx-confirm "Permanently delete your account?"}
         (components/button-warning (i18n "Delete Account"))]]])))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (if (-> req :session :id)
       (let [req (assoc req :query-fn query-fn)]
         (page-htmx
          {:css ["/output.css"] :hyperscript? true}
          (preferences req)))
       (response/redirect "/")))))
