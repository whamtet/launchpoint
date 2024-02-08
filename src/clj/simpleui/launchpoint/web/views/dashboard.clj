(ns simpleui.launchpoint.web.views.dashboard
    (:require
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.controllers.user :as user]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.components.dropdown :as dropdown]))

(defn main-dropdown [first_name]
  [:div.absolute.top-1.right-1
   (dropdown/dropdown
    first_name
    [[:div.p-1 {:hx-post "/api/logout"}
      (i18n "Logout")]])])

(defcomponent ^:endpoint dashboard [req command]
  (case command
        (let [{:keys [first_name]} (user/get-user req)]
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
           [:div.w-96.border.rounded-lg.absolute.top-20.left-10.p-1.text-gray-500
            [:a {:href "/profile"} "Create profile..."]]
           ])))
