(ns simpleui.launchpoint.web.htmx
  (:require
   [hiccup.core :as h]
   [hiccup.page :as p]
   [ring.util.http-response :as http-response]
   [simpleui.core :as simpleui]
   [simpleui.launchpoint.env :refer [dev?]]
   [simpleui.launchpoint.i18n :refer [i18n]]
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

(defn- unminify [^String s]
  (if dev?
    (.replace s ".min" "")
    s))

(defn page-htmx [{:keys [css js hyperscript? freeimage?]} & body]
  (page
   [:head
    [:meta {:charset "UTF-8"}]
    [:title (i18n "SimpleUI Launchpoint")]
    [:link {:rel "icon" :href "/logo_dark.svg"}]
    (for [sheet css]
      [:link {:rel "stylesheet" :href (resource-cache/cache-suffix sheet)}])]
   [:body
    (render/walk-attrs body)
    [:script {:src
              (unminify "https://unpkg.com/htmx.org@1.9.5/dist/htmx.min.js")}]
    [:script "htmx.config.defaultSwapStyle = 'outerHTML';"]
    (for [js js]
      [:script {:src js}])
    (when freeimage?
          [:script {:async true :src "//freeimage.host/sdk/pup.js" :data-url "https://freeimage.host/upload"}])
    (when hyperscript?
          [:script {:src (unminify "https://unpkg.com/hyperscript.org@0.9.12/dist/_hyperscript.min.js")}])]))

(defn page-simple [{:keys [css]} & body]
  (page
   [:head
    [:meta {:charset "UTF-8"}]
    (for [sheet css]
      [:link {:rel "stylesheet" :href (resource-cache/cache-suffix sheet)}])]
   [:body (render/walk-attrs body)]))

(defmacro defcomponent
  [name [req :as args] & body]
  (if-let [sym (simpleui/symbol-or-as req)]
    `(simpleui/defcomponent ~name ~args
      (let [{:keys [~'session ~'path-params ~'query-fn]} ~sym
            ~'params (merge ~'params ~'path-params)
            {:keys [~'id]} ~'session]
        ~@body))
    (throw (Exception. "req ill defined"))))
