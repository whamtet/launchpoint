(ns simpleui.launchpoint.web.views.login
    (:require
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]))

;;
[:div.border-r.bg-slate-200]

(defn- grey-out [s grey?]
  (if grey?
    (str s " bg-slate-200")
    s))

(defcomponent ^:endpoint login [req register]
  [:div {:hx-target "this"}
   [:a.absolute.top-3.right-3 {:href ""}
    [:img.w-24 {:src "/logo.svg"}]]
   [:div {:class "mt-24
   mx-auto w-1/2
   border rounded-lg"}
    [:div.border-b.flex.text-center
     [:div {:class (grey-out "w-1/2 border-r p-1" register)}
      [:a {:href "" :hx-get "login"}
       (i18n "Login")]]
     [:div {:class (grey-out "w-1/2 p-1" (not register))}
      [:a {:href "" :hx-get "login" :hx-vals {:register true}}
       (i18n "Register")]]]]])
