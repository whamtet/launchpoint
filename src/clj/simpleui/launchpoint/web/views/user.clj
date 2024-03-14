(ns simpleui.launchpoint.web.views.user
    (:require
      [simpleui.core :as simpleui]
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.controllers.item-order :as item-order]
      [simpleui.launchpoint.web.controllers.profile :as profile]
      [simpleui.launchpoint.web.controllers.user :as user]
      [simpleui.launchpoint.web.htmx :refer [page-htmx]]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.dashboard :as dashboard :refer [gravatar]]
      [simpleui.launchpoint.web.views.profile.history :refer [months]]
      [simpleui.launchpoint.util :as util]
      [simpleui.response :as response]))

(defn- pic [email]
  [:div.my-3.border.rounded-lg.inline-block.overflow-hidden.relative
   [:img {:src (gravatar email)}]])

(defn- names [user_id first_name last_name pdf?]
  [:div
   [:span.text-2xl first_name " " last_name]
   (when-not pdf?
             [:a.ml-2 {:href (str "/api/profile-pdf/" user_id)
                       :target "_blank"}
              (components/button (i18n "View PDF"))])])

(defn- description-section [description]
  [:div
   [:div.text-center
    (components/h3 (i18n "Description"))]
   [:div
    {:class "my-3"} description]])

(defn- work-history [jobs]
  [:div {:class "w-3/4"}
   (util/map-last
    (fn [last? {:keys [title
                       company src
                       present
                       from-year from-month
                       to-year to-month
                       description] :as job}]
      [:div
       [:div.my-1
        (components/h3 title)]
       [:div.flex.items-center.my-1
        [:span.mr-4.text-lg company]
        (when src [:img.w-9 {:src (str "/api/company/" src)}])]
       [:div.my-1
        (if (pos? from-month)
          (list ((months) from-month) " " from-year " - ")
          (list from-year " - "))
        (cond
         present (i18n "Present")
         (pos? to-month)
         [:span ((months) to-month) " " to-year]
         :else to-year)]
       [:div.my-2 description]
       (when-not last? [:hr {:class "my-3 border w-1/2"}])])
    jobs)])

(defn- education-history [education]
  [:div {:class "my-3 w-3/4"}
   (util/map-last
    (fn [last? {:keys [degree institution year] :as education}]
      [:div
       [:div.my-1
        [:span.text-lg degree]]
       [:div.my-1
        [:span.text-lg institution]]
       [:div.my-1 year]
       (when-not last? [:hr {:class "my-3 border w-1/2"}])])
    education)])

[:div {:class "w-2/3"}]
(defn- page-width [style pdf?]
  (if pdf?
    style
    (str style " " "w-2/3")))

(defn profile [req pdf?]
  (let [{:keys [first_name last_name email]} (user/get-user req)
        {{:keys [user-id]} :path-params} req
        {:keys [description jobs education]} (profile/get-cv req)
        basket-count (item-order/basket-count req)]
    [:div.min-h-screen.p-1 {:_ "on click add .hidden to .drop"}
     (when-not pdf?
               (list
                [:a.absolute.top-3.left-3 {:href "/"}
                 [:img.w-24 {:src "/logo.svg"}]]
                (dashboard/main-dropdown basket-count first_name)))
     [:div {:class (page-width "mx-auto
     border border-gray-300 rounded-md shadow-xl
     flex flex-col items-center" pdf?)}
      (pic email)
      (names user-id first_name last_name pdf?)
      [:hr.w-96.my-6.border]
      (description-section description)
      (components/h3 (i18n "Work History"))
      (work-history jobs)
      [:div.mt-3
       (components/h3 (i18n "Education"))]
      (education-history education)
      ]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (if (-> req :session :id)
       (let [req (assoc req :query-fn query-fn)]
         (page-htmx
          {:css ["/output.css"] :hyperscript? true}
          (profile req false)))
       (response/redirect "/")))))
