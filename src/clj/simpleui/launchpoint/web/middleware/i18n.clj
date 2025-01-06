(ns simpleui.launchpoint.web.middleware.i18n)

(def ^:dynamic *lang* nil)

;; fr-CH, fr;q=0.9, en;q=0.8, de;q=0.7, *;q=0.5
(defn- default-lang [req]
  (re-find #"en|ja" (get-in req [:headers "accept-language"])))

(defn set-lang [handler]
  (fn [req]
    (binding [*lang* (or
                      (-> req :session :lang)
                      (default-lang req)
                      "en")]
      (handler req))))
