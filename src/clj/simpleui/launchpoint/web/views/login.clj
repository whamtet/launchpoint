(ns simpleui.launchpoint.web.views.login
    (:require
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.util :refer [defcss]]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.components.dropdown :as dropdown]
      [simpleui.launchpoint.web.views.lang :as lang]
      [simpleui.launchpoint.web.controllers.login :as controllers.login]))

(defcss [:div.bg-clj-blue-light.text-gray-500])
(defn- highlight [s highlighted?]
  (str s
       (if highlighted?
         " bg-clj-blue-light"
         " text-gray-500")))

(defn- warning [msg]
  [:div.m-1 (components/warning msg)])

(defn login-form [email problem]
  [:form.p-2 {:hx-post "login:login"}
   (components/email (i18n "Email") "email" email)
   (components/password (i18n "Password") "password")
   (when (= :unknown problem)
         (warning (i18n "Unknown account")))
   (components/submit (i18n "Login"))])

(defn- registration-form [first-name last-name email problem]
  [:form#registration-form.p-2 {:hx-post "login:register"}
   (components/hidden "register" true)
   (components/text (i18n "First name") "first-name" first-name :required)
   (components/text (i18n "Last name") "last-name" last-name :required)
   (components/email (i18n "Email") "email" email :required)
   (when (= :duplicate-email problem)
         (warning (i18n "Please choose another email")))
   (components/password (i18n "Password") "password" :required)
   (when (= :pw-quality problem)
         (warning (i18n "Password requires at least 8 letters and numbers")))
   (components/password (i18n "Password Confirm") "password2" :required)
   (when (= :pw-match problem)
         (warning (i18n "Passwords must match")))
   (components/submit (i18n "Register"))])

(defn- login-disp [req register first-name last-name email problem]
  [:div.pt-6.pb-12.relative.min-h-screen {:hx-target "this" :_ "on click add .hidden to .drop"}
   [:a.absolute.top-3.left-3 {:href ""}
    [:img.w-24 {:src "/logo.svg"}]]
   [:div.text-center
    (components/h1 (i18n "SimpleUI Launchpoint"))]
   [:a.absolute.top-3.right-3 {:href "https://github.com/whamtet/launchpoint"
                               :target "_blank"}
    [:img.w-8.opacity-70 {:src "/github-mark.png"}]]
   [:div {:class "mt-12
   mx-auto w-1/2
   border rounded-lg overflow-hidden"}
    [:div.border-b.flex.text-center
     [:a {:href ""
          :hx-get "login"
          :class (highlight "w-1/2 border-r p-1" (not register))}
      (i18n "Login")]
     [:a#register {:href ""
                   :hx-get "login"
                   :hx-vals {:register true}
                   :class (highlight "w-1/2 p-1" register)}
      (i18n "Register")]]
    (if register
      (registration-form first-name last-name email problem)
      (login-form email problem))]
   ;; language selector
   (lang/lang-dropup req)])

(defmacro or-keyword [test alternative]
  `(let [~'$ ~test]
    (if (keyword? ~'$) ~alternative ~'$)))

(defcomponent ^:endpoint login [req
                                register
                                first-name
                                last-name
                                email
                                password
                                password2
                                command]
  lang/lang-dropup ;; to ensure resolution
  (case command
        "login"
        (or-keyword
         (controllers.login/login req email password)
         (login-disp req register first-name last-name email $))
        "register"
        (or-keyword
         (controllers.login/register req first-name last-name email password password2)
         (login-disp req register first-name last-name email $))
        (login-disp req register first-name last-name email nil)))
