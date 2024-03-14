(ns simpleui.launchpoint.web.middleware.i18n)

(def ^:dynamic *lang* nil)

(defn set-lang [handler]
  (fn [req]
    (binding [*lang* (-> req :session :lang)]
      (handler req))))
