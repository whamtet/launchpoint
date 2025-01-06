(ns simpleui.launchpoint.web.views.lang
    (:require
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.middleware.i18n :as middleware.i18n]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.components.dropdown :as dropdown]
      [simpleui.launchpoint.web.controllers.login :as controllers.login]))

(def lang-disp
  {"en" "English"
   "ja" "日本語"})

(defn dropup [label m]
  [:div
   [:div {:class "drop hidden rounded-lg border p-1.5 bg-white"}
    [:a {:href "https://github.com/whamtet/launchpoint#internationalization"
         :target "_blank"}
     [:div {:class "p-2 hover:bg-slate-100"}
      "Add your language..."]]
    (for [[new-lang v] m]
      [:a {:href "" :hx-get "lang-dropup" :hx-vals {:new-lang new-lang}}
       [:div {:class "p-1 hover:bg-slate-100"}
        v]])]
   (dropdown/button-up label)])

(defcomponent ^:endpoint lang-dropup [req ^:nullable new-lang]
  (if top-level?
    (controllers.login/assoc-lang (:session req) new-lang)
    [:div.absolute.bottom-0.flex.w-full.justify-center
     (dropup
      (lang-disp middleware.i18n/*lang*)
      (dissoc lang-disp middleware.i18n/*lang*))]))
