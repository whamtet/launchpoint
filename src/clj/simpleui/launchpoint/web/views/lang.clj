(ns simpleui.launchpoint.web.views.lang
    (:require
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.components.dropdown :as dropdown]
      [simpleui.launchpoint.web.controllers.login :as controllers.login]))

(def lang-disp
  {nil "English"
   "jp" "日本語"})

(defcomponent ^:endpoint lang-dropup [req ^:nullable new-lang]
  (if top-level?
    (controllers.login/assoc-lang (:session req) new-lang)
    [:div.absolute.bottom-0.flex.w-full.justify-center
     (dropdown/dropup
      (lang-disp lang)
      (for [[new-lang disp] (dissoc lang-disp lang)]
        [(str "lang-dropup?new-lang=" new-lang) disp]))]))
