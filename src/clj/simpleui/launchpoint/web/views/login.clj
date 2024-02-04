(ns simpleui.launchpoint.web.views.login
    (:require
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]))

[:div.bg-clj-blue-light.text-gray-500]
(defn- highlight [s highlighted?]
  (str s
       (if highlighted?
         " bg-clj-blue-light"
         " text-gray-500")))

(def login-form
  [:form.pt-3 {:hx-post "login:login"}
   (components/email (i18n "Email") "email")
   (components/password (i18n "Password") "password")
   (components/submit (i18n "Login"))])

(defn registration-form [first-name last-name email]
  [:form.pt-3 {:hx-post "login:register"}
   (components/hidden "register" true)
   (components/text (i18n "First name") "first-name" first-name :required)
   (components/text (i18n "Last name") "last-name" last-name :required)
   (components/email (i18n "Email") "email" email :required)
   (components/password (i18n "Password") "password" :required)
   (components/password (i18n "Password Confirm") "password2" :required)
   (components/submit (i18n "Register"))])

(defcomponent ^:endpoint login [req
                                register
                                first-name
                                last-name
                                email
                                password
                                password2
                                command]
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
                :class (highlight "w-1/2 border-r p-1" (not register))}
             (i18n "Login")]
           [:a {:href ""
                :hx-get "login"
                :hx-vals {:register true}
                :class (highlight "w-1/2 p-1" register)}
             (i18n "Register")]]
          (if register
            (registration-form first-name last-name email)
            login-form)]]))
