(ns simpleui.launchpoint.web.controllers.login
  (:require
    [simpleui.launchpoint.util :refer [deftrim]]
    simpleui.response
    [crypto.password.bcrypt :as password]))

;; password/encrypt
;; (password/check password encrypted)

(defn- refresh-session [session]
  (assoc simpleui.response/hx-refresh :session session))

(defn- assoc-session [session & keys]
  (refresh-session
   (apply assoc session keys)))

(defn- dissoc-session [session & keys]
  (refresh-session
   (apply dissoc session keys)))

(def pw-regex #"(?=.*[A-Za-z])(?=.*\d).{8,}")

(defn duplicate-email? [e]
  (-> e str (.contains "(UNIQUE constraint failed: user.email)")))

(deftrim register [{:keys [query-fn session]}
                   ^:trim first-name
                   ^:trim last-name
                   ^:trim email
                   ^:trim password
                   ^:trim password2]
  (cond
   (not= password password2) :pw-match
   (not (re-find pw-regex password)) :pw-quality
   :else
   (try
     (->> password
          password/encrypt
          (hash-map :first-name first-name
                    :last-name last-name
                    :q (.toLowerCase (str first-name " " last-name))
                    :email email
                    :password)
          (query-fn :insert-user)
          first
          :id
          (assoc-session session :id))
     (catch clojure.lang.ExceptionInfo e
       (if (duplicate-email? e)
         :duplicate-email
         (throw e))))))

(defn logout [session]
  (dissoc-session session :id))

(defn login [{:keys [query-fn session]}
             email
             password]
  (if-let [record (query-fn :get-user-by-email {:email email})]
    (if (->> record :password (password/check password))
      (assoc-session session :id (:id record))
      :unknown)
    :unknown))
