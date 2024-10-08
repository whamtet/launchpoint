(ns simpleui.launchpoint.web.views.item
    (:require
      [simpleui.core :as simpleui]
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.controllers.iam :as iam]
      [simpleui.launchpoint.web.controllers.item-order :as item-order]
      [simpleui.launchpoint.web.controllers.store :as store]
      [simpleui.launchpoint.web.controllers.user :as user]
      [simpleui.launchpoint.web.htmx :refer [page-htmx defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.dashboard :as dashboard]
      [simpleui.launchpoint.web.views.profile.history :as profile.history]
      [simpleui.response :as response]))

(defcomponent ^:endpoint item [req command ^:long inventory_id]
  (case command
        "buy"
        (iam/do-auth
         (item-order/add-order req inventory_id)
         (response/hx-redirect "/checkout/"))
        (let [{:keys [first_name]} (user/get-user req)
              {:keys [id image title description]} (store/get-item req)
              basket-count (item-order/basket-count req)]
          [:div.min-h-screen.p-1 {:_ "on click add .hidden to .drop"}
           [:a.absolute.top-3.left-3 {:href "/"}
            [:img.w-24 {:src "/logo.svg"}]]
           (dashboard/main-dropdown basket-count first_name)
           [:div {:class "w-2/3 mx-auto"}
            [:div.flex.mt-12
             [:div {:class "w-1/3"}
              [:img {:src image}]]
             [:div {:class "p-3 w-2/3"}
              [:div.my-3.text-center (components/h3 title)]
              [:p.text-gray-700 description]
              [:div.mt-3 {:hx-post "item:buy"
                          :hx-vals {:inventory_id id}}
               (components/button (i18n "Add to cart"))]]]]])))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (if (-> req :session :id)
       (let [req (assoc req :query-fn query-fn)]
         (page-htmx
          {:css ["/output.css"] :hyperscript? true}
          (item req)))
       (response/redirect "/")))))
