(ns simpleui.launchpoint.web.controllers.login
  (:require
    simpleui.response
    [crypto.password.bcrypt :as password]))

;; password/encrypt
;; (password/check password encrypted)

(defn- refresh-session [session]
  (assoc simpleui.response/hx-refresh :session session))

(defn- assoc-session [session & keys]
  (refresh-session
   (apply assoc session keys)))
