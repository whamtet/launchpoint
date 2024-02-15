(ns simpleui.launchpoint.web.views.dashboard
    (:require
      [clj-commons.digest :as digest]
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.controllers.profile :as profile]
      [simpleui.launchpoint.web.controllers.user :as user]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.components.dropdown :as dropdown]))

(defn gravatar
  ([^String email] (gravatar email 256))
  ([^String email size]
   (when email
         (format "https://gravatar.com/avatar/%s?s=%s"
                 (-> email .trim .toLowerCase digest/sha256)
                 size))))

(defn main-dropdown [first_name]
  [:div.absolute.top-1.right-1
   (dropdown/dropdown
    first_name
    [[:div.p-1 {:hx-post "/api/logout"}
      (i18n "Logout")]])])

(defcomponent ^:endpoint dashboard [req command]
  (case command
        (let [{:keys [first_name email]} (user/get-user req)
              {:keys [description]} (profile/get-cv req)]
          [:div.min-h-screen.p-1 {:_ "on click add .hidden to .drop"}
           [:a.absolute.top-3.left-3 {:href ""}
            [:img.w-24 {:src "/logo.svg"}]]
           ;; search
           [:div {:class "w-1/2 mx-auto"}
            [:input {:class "w-full border rounded-full p-2"
                     :type "text"
                     :placeholder "Search..."}]]
           ;; dropdown
           (main-dropdown first_name)
           ;; profile panel
           [:a {:href "/profile"}
            [:div.w-96.border.rounded-lg.absolute.top-20.left-10.p-1.text-gray-500
             [:div
              [:img.mx-auto {:src (gravatar email)}]]
             [:div.border-t.mt-2.p-2
              description]]]
           ])))
