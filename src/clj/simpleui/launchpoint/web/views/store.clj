(ns simpleui.launchpoint.web.views.store
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
      [simpleui.response :as response]
      [simpleui.rt :as rt]))

(defn- number-hx [index label hx-post name value]
  [:div
   [:div.p-1 label]
   [:div.p-1
    [:input.w-full.p-1
     {:type "number"
      :name name
      :value value
      :hx-post hx-post
      :hx-vals {:index index}
      :placeholder label}]]])

(defcomponent ^:endpoint item [req ^:long index title ^:double price ^:long count description image command]
  (case command
        "price" (iam/do-auth-nil
                 (store/upsert-price req index price))
        "count" (iam/do-auth-nil
                 (store/upsert-count req index count))
        [:div
         [:div.flex
          [:img.w-72 {:src image}]
          [:div.mx-12.flex.flex-col.pt-6
           [:div.text-center.text-xl.mb-6 title]
           [:div.text-gray-500.mb-6 description]
           [:hr.border.mb-4]
           [:div.flex
            [:div {:class "w-1/2"}
             (number-hx
              index (i18n "Price ($)") "item:price" "price" price)]
            [:div {:class "w-1/2"}
             (number-hx
              index (i18n "Quantity") "item:count" "count" count)]]
           ]]
         [:hr.border.my-4]]))

(defcomponent store [req]
  (let [{:keys [first_name]} (user/get-user req)
        basket-count (item-order/basket-count req)]
    [:div.min-h-screen.p-1 {:_ "on click add .hidden to .drop"}
     [:a.absolute.top-3.left-3 {:href "/"}
      [:img.w-24 {:src "/logo.svg"}]]
     (dashboard/main-dropdown basket-count first_name)
     [:div.mt-12
       (rt/map-indexed item req (store/items req))]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (if (-> req :session :id)
       (->> (assoc req :query-fn query-fn)
            store
            (page-htmx {:css ["/output.css"] :hyperscript? true}))
       (response/redirect "/")))))
