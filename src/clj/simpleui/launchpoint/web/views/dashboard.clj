(ns simpleui.launchpoint.web.views.dashboard
    (:require
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.controllers.login :as login]
      [simpleui.launchpoint.web.views.components :as components]))

(defcomponent ^:endpoint dashboard [req command]
  (case command
        "logout" (login/logout session)
        [:div
         [:a {:href "#" :hx-post "dashboard:logout"}
          (components/button
           (i18n "Logout"))]]))
