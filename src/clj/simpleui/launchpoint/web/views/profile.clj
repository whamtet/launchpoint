(ns simpleui.launchpoint.web.views.profile
    (:require
      [clj-commons.digest :as digest]
      [simpleui.core :as simpleui]
      [simpleui.launchpoint.web.controllers.profile :as profile]
      [simpleui.launchpoint.web.controllers.user :as user]
      [simpleui.launchpoint.web.htmx :refer [page-htmx defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.dashboard :as dashboard]
      [simpleui.response :as response]))

(defn- gravatar [^String email]
  (some->> email
           .trim
           .toLowerCase
           digest/sha256
           (format "https://gravatar.com/avatar/%s?s=256")))

(defcomponent ^:endpoint pic [req email]
  (if top-level?
    (components/modal "w-1/2"
                      [:div.flex
                       [:div.m-2.border.rounded-lg.inline-block.overflow-hidden
                        [:img {:src (gravatar email)}]]])
    [:a {:href ""
         :hx-post "pic"
         :hx-target "#modal"
         :hx-vals {:email email}}
     [:div.m-2.border.rounded-lg.inline-block.overflow-hidden
      [:img {:src (gravatar email)}]]]))

(defcomponent ^:endpoint profile [req command]
  (case command
        "create" (prn (profile/upsert req))
        (let [{:keys [first_name last_name email]} (user/get-user req)]
          [:div.min-h-screen.p-1 {:_ "on click add .hidden to .drop"}
           (dashboard/main-dropdown first_name)
           [:div {:class "min-h-screen w-2/3 mx-auto"}
            [:div#modal]
            (pic req email)]])))

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
