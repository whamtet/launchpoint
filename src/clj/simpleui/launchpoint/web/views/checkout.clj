(ns simpleui.launchpoint.web.views.checkout
    (:require
      [simpleui.core :as simpleui]
      [simpleui.launchpoint.i18n :refer [i18n i18n-map]]
      [simpleui.launchpoint.web.controllers.iam :as iam]
      [simpleui.launchpoint.web.controllers.item-order :as item-order]
      [simpleui.launchpoint.web.controllers.user :as user]
      [simpleui.launchpoint.web.htmx :refer [page-htmx defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.dashboard :as dashboard]
      [simpleui.response :as response]
      [simpleui.launchpoint.util :as util]))

(defn button [label]
  [:span.p-1
   [:button {:type "button"
             :class "w-8 bg-clj-blue py-1.5 px-3 rounded-lg text-white"}
    label]])

(defn- format$ [x]
  (format "%.2f" x))

(defn- checkout-row [{:keys [id title image price quantity]}]
  [:tr
   [:td
    [:img {:class "w-24"
           :src image}]]
   [:td
    [:span.mx-7 title]]
   [:td
    [:div.flex.items-center
     [:div {:hx-post "checkout:inc"
            :hx-vals {:inventory_id id}}
      (button "+")]
     [:div.mx-3 quantity]
     [:div {:hx-post "checkout:dec"
            :hx-vals {:inventory_id id}}
      (button "-")]]]
   [:td
    [:div.mx-2 (format$ (* price quantity))]]])

(defn- subtotal [orders]
  (->> orders
       (map #(* (:price %) (:quantity %)))
       (apply +)))

(defn payment-form [orders]
  [:form#payment-form.w-96.mx-auto
   [:div#payment-element]
   (components/h2 (i18n "Total") " $" (format$ (subtotal orders)))
   [:button#submit {:class "mt-2 bg-clj-blue py-1.5 px-3 rounded-lg text-white"}
     [:div#spinner.spinner.hidden]
     [:span#button-text (i18n "Pay now")]]
   [:div#payment-message.hidden]])

(defn i18n-script []
  [:script
   (format "i18n = %s"
           (i18n-map
            {"unexpected_error" "An unexpected error occurred."
             "payment_succeeded" "Payment succeeded!"
             "processing" "Your payment is processing."
             "requires_payment_method" "Your payment was not successful, please try again."
             "wrong" "Something went wrong."}))])

(defcomponent ^:endpoint checkout [req command ^:long inventory_id]
  (iam/do-auth
   (case command
         "inc" (item-order/inc-order req inventory_id)
         "dec"
         (when-not (pos? (item-order/dec-order req inventory_id))
                   (item-order/del-order req inventory_id))
         nil))
  (let [{:keys [first_name]} (user/get-user req)
        basket-count (item-order/basket-count req)]
    [:div.min-h-screen.p-1 {:_ "on click add .hidden to .drop"
                            :hx-target "this"}
     [:a.absolute.top-3.left-3 {:href "/"}
      [:img.w-24 {:src "/logo.svg"}]]
     (dashboard/main-dropdown basket-count first_name)
     [:div {:class "w-2/3 mx-auto text-center"}
      [:div.my-5 (components/h1 (i18n "Checkout"))]
      (if-let [orders (item-order/my-order req)]
        [:div
         [:table.mx-auto
          [:tbody
           (map checkout-row orders)]]
         [:hr.mt-3.border]
         [:p.my-3.text-gray-700 (i18n "Try card 4242 4242 4242 4242")]
         (payment-form orders)]
        (i18n "Checkout is empty"))]
     (if (simpleui/post? req)
       [:script "initialize()"]
       (i18n-script))]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (if (-> req :session :id)
       (let [req (assoc req :query-fn query-fn)]
         (page-htmx
          {:css ["/output.css"] :hyperscript? true :stripe? true}
          (checkout req)))
       (response/redirect "/")))))
