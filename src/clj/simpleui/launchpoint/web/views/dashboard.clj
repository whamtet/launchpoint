(ns simpleui.launchpoint.web.views.dashboard
    (:require
      [clj-commons.digest :as digest]
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.util :refer [$format]]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.controllers.item-order :as item-order]
      [simpleui.launchpoint.web.controllers.profile :as profile]
      [simpleui.launchpoint.web.controllers.store :as store]
      [simpleui.launchpoint.web.controllers.user :as user]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.components.dropdown :as dropdown]
      [simpleui.launchpoint.web.views.icons :as icons]))

(defn gravatar
  ([^String email] (gravatar email 256))
  ([^String email size]
   (when email
         (format "https://gravatar.com/avatar/%s?s=%s"
                 (-> email .trim .toLowerCase digest/sha256)
                 size))))

(defn main-dropdown [basket-count first_name]
  [:div.absolute.top-1.right-1.flex
   [:div.p-1
    (if (pos? basket-count)
     [:a {:href "/checkout/"
          :class "bg-clj-green-light text-white p-2 inline-block rounded-lg flex items-end"}
      icons/cart
      [:span {:class "text-xs relative left-0.5 top-1"} basket-count]]
     [:a {:class "bg-clj-green-light text-white p-2 inline-block rounded-lg flex items-end opacity-50"}
      icons/cart])]
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
  (->> (concat (user/search-users req q) (store/search-items q))
       (sort-by :q)
       (take 5)))

(defn- search-result [{:keys [user_id email first_name last_name
                              id image title description]}]
  (if user_id
    [:a {:href ($format "/user/{user_id}/")}
     [:div {:class "flex hover:bg-slate-100"}
      [:img.w-20 {:src (gravatar email 80)}]
      [:div.p-3.w-full.text-gray-700.text-lg.flex.flex-col.justify-center
       first_name " " last_name]]]
    [:a {:href ($format "/item/{id}/")}
     [:div {:class "flex hover:bg-slate-100"}
      [:img.w-20 {:src image}]
      [:div.p-3.w-full.text-gray-700.text-lg.flex.flex-col.justify-center title]]]))

(defcomponent ^:endpoint search [req q]
  (if top-level?
    (when (some-> q .trim count (>= 3))
          [:div#search-results
           {:class "drop rounded-lg border p-1 absolute top-18 w-full bg-white z-10"}
           (->> q
                .trim
                .toLowerCase
                (combine-searches req)
                (map search-result))])
    [:div {:class "w-1/2 mx-auto relative"}
     [:input {:class "w-full border rounded-full p-3"
              :type "text"
              :hx-get "search"
              :name "q"
              :value q
              :hx-trigger "focus,keyup changed delay:0.5s"
              :hx-target "#search-results"
              :_ "on click halt"
              :autocomplete "off"
              :placeholder "Search users and products..."}]
     [:div#search-results]]))

(defcomponent ^:endpoint dashboard [req command]
  (case command
        (let [{:keys [first_name email]} (user/get-user req)
              {:keys [description]} (profile/get-cv req)
              basket-count (item-order/basket-count req)]
          [:div.min-h-screen.p-2 {:_ "on click add .hidden to .drop"}
           [:a.absolute.top-2.left-2 {:href ""}
            [:img.w-24 {:src "/logo.svg"}]]
           ;; search
           (search req)
           ;; dropdown
           (main-dropdown basket-count first_name)
           ;; profile panel
           [:a {:href "/profile"}
            [:div.w-96.border.rounded-lg.absolute.top-20.left-10.p-1.text-gray-500
             [:div
              [:img.mx-auto {:src (gravatar email)}]]
             [:div.border-t.mt-2.p-2
              description]]]
           ])))
