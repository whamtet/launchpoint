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

(defn- scripts [{:keys [js hyperscript? stripe? google?]}]
  (cond-> (map #(vector :script {:src (resource-cache/cache-suffix %)}) js)
          hyperscript? (conj
                        [:script {:src (unminify "https://unpkg.com/hyperscript.org@0.9.12/dist/_hyperscript.min.js")}])
          stripe? (conj [:script {:src (resource-cache/cache-suffix "/checkout.js")}]
                        [:script {:src "https://js.stripe.com/v3/"}])
          google? (conj [:script {:src "https://accounts.google.com/gsi/client" :async true :defer true}])))

(defn page-htmx [opts & body]
  (page
   [:head
    [:meta {:charset "UTF-8"}]
    [:title (i18n "SimpleUI Launchpoint")]
    [:link {:rel "icon" :href "/logo_dark.svg"}]
    (when (:stripe? opts)
          [:link {:rel "stylesheet" :href "/checkout.css"}])
    (for [sheet (:css opts)]
      [:link {:rel "stylesheet" :href (resource-cache/cache-suffix sheet)}])]
   [:body
    (render/walk-attrs body)
    [:script {:src
              (unminify "https://unpkg.com/htmx.org@1.9.5/dist/htmx.min.js")}]
    [:script "htmx.config.defaultSwapStyle = 'outerHTML';"]
    (scripts opts)]))

(defn page-simple [{:keys [css]} & body]
  (page
   [:head
    [:meta {:charset "UTF-8"}]
    (for [sheet css]
      [:link {:rel "stylesheet" :href (resource-cache/cache-suffix sheet)}])]
   [:body (render/walk-attrs body)]))

(defn redirect-tab [r1 r2]
  (page
    [:body
     [:script
      (format "if (open('%s')) {location = '%s'}" r1 r2)]
     (i18n "Please allow popups then refresh this page.")]))

(defmacro defcomponent
  [name [req :as args] & body]
  (if-let [sym (simpleui/symbol-or-as req)]
    `(simpleui/defcomponent ~name ~args
      (let [{:keys [~'session ~'path-params ~'query-fn]} ~sym
            ~'params (merge ~'params ~'path-params)
            {:keys [~'id ~'lang]} ~'session]
        ~@body))
    (throw (Exception. "req ill defined"))))
