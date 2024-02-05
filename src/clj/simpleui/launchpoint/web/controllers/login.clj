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

(def pw-regex #"(?=.*[A-Za-z])(?=.*\d).{8,}")

(defn duplicate-email? [e]
  (-> e str (.contains "(UNIQUE constraint failed: user.email)")))

(defn register [query-fn
                first-name
                last-name
                email
                password
                password2]
  (cond
   (not= password password2) :pw-match
   (not (re-find pw-regex password)) :pw-quality
   :else
   (try
     (query-fn
      :insert-user
      {:first-name first-name
       :last-name last-name
       :email email
       :password (password/encrypt password)})
     nil
     (catch clojure.lang.ExceptionInfo e
       (if (duplicate-email? e)
         :duplicate-email
         (throw e))))))
