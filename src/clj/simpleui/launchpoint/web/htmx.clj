(ns simpleui.launchpoint.web.htmx
  (:require
   [simpleui.render :as render]
   [ring.util.http-response :as http-response]
   [hiccup.core :as h]
   [hiccup.page :as p]))

(defn page [opts & content]
  (-> (p/html5 opts content)
      http-response/ok
      (http-response/content-type "text/html")))

(defn ui [opts & content]
  (-> (h/html opts content)
      http-response/ok
      (http-response/content-type "text/html")))

(defn- htmx []
  [:script {:src
            (if false ;(dev?)
              "/htmx.js"
              "https://unpkg.com/htmx.org@1.9.5/dist/htmx.min.js")}])

(defn page-htmx [{:keys [css js google?]} & body]
  (page
   [:head
    [:meta {:charset "UTF-8"}]
    [:title "Launchpoint"]
    [:link {:rel "icon" :href "/favicon.ico"}]]
   [:body
    (render/walk-attrs body)
    (htmx)
    [:script "htmx.config.defaultSwapStyle = 'outerHTML';"]
    (for [js js]
      [:script {:src js}])
    (when google?
          [:script {:src "https://accounts.google.com/gsi/client" :async true :defer true}])]))
