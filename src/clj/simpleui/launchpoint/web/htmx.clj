(ns simpleui.launchpoint.web.htmx
  (:require
   [hiccup.core :as h]
   [hiccup.page :as p]
   [ring.util.http-response :as http-response]
   [simpleui.launchpoint.web.resource-cache :as resource-cache]
   [simpleui.render :as render]))

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
    [:title "SimpleUI Launchpoint"]
    [:link {:rel "icon" :href "/favicon.ico"}]
    (for [sheet css]
      [:link {:rel "stylesheet" :href (resource-cache/cache-suffix sheet)}])]
   [:body
    (render/walk-attrs body)
    (htmx)
    [:script "htmx.config.defaultSwapStyle = 'outerHTML';"]
    (for [js js]
      [:script {:src js}])
    (when google?
          [:script {:src "https://accounts.google.com/gsi/client" :async true :defer true}])]))
