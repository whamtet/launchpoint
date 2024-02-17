(ns simpleui.launchpoint.web.views.dashboard
    (:require
      [clj-commons.digest :as digest]
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.controllers.profile :as profile]
      [simpleui.launchpoint.web.controllers.store :as store]
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
    [[:a {:href "/"}
      [:div.p-2
       (i18n "Dashboard")]]
     [:a {:href "/profile/"}
      [:div.p-2
       (i18n "My Profile")]]
     [:a {:href "/store/"}
      [:div.p-2
        (i18n "Manage Store")]]
     [:a {:href ""}
      [:div.p-2 {:hx-post "/api/logout"}
        (i18n "Logout")]]])])

(defn combine-searches [req q]
  (user/search-users req q))

(defcomponent ^:endpoint search [req q]
  (if top-level?
    (when (some-> q .trim count (> 3))
          [:div#search-results
           (for [result (->> q .trim .toLowerCase (combine-searches req))]
             [:div (pr-str result)])])
    [:div {:class "w-1/2 mx-auto"}
     [:input {:class "w-full border rounded-full p-3"
              :type "text"
              :hx-get "search"
              :name "q"
              :value q
              :hx-trigger "focus,keyup changed delay:0.5s"
              :hx-target "#search-results"
              :placeholder "Search..."}]
     [:div#search-results]]))

(defcomponent ^:endpoint dashboard [req command]
  (case command
        (let [{:keys [first_name email]} (user/get-user req)
              {:keys [description]} (profile/get-cv req)]
          [:div.min-h-screen.p-2 {:_ "on click add .hidden to .drop"}
           [:a.absolute.top-2.left-2 {:href ""}
            [:img.w-24 {:src "/logo.svg"}]]
           ;; search
           (search req)
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
