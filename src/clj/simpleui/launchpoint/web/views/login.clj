(ns simpleui.launchpoint.web.views.login
    (:require
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]))

;;
[:div.border-r.bg-slate-200]

(defn- grey-out [s grey?]
  (if grey?
    (str s " bg-slate-200")
    s))

(def login-form
  [:form.pt-3 {:hx-post "login:login"}
   (components/email (i18n "Email") "email")
   (components/password (i18n "Password") "password")
   (components/submit (i18n "Login"))])

(def registration-form [:div])

(defcomponent ^:endpoint login [req register email password command]
  (case command
        "login" (prn 'xx email password)
        [:div {:hx-target "this"}
         [:a.absolute.top-3.right-3 {:href ""}
          [:img.w-24 {:src "/logo.svg"}]]
         [:div {:class "mt-24
   mx-auto w-1/2
   border rounded-lg overflow-hidden"}
          [:div.border-b.flex.text-center
           [:a {:href ""
                :hx-get "login"
                :class (grey-out "w-1/2 border-r p-1" register)}
             (i18n "Login")]
           [:a {:href ""
                :hx-get "login"
                :hx-vals {:register true}
                :class (grey-out "w-1/2 p-1" (not register))}
             (i18n "Register")]]
          (if register
            registration-form
            login-form)]]))
