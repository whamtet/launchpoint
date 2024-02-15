(ns simpleui.launchpoint.web.views.item
    (:require
      [simpleui.core :as simpleui]
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.controllers.store :as store]
      [simpleui.launchpoint.web.htmx :refer [page-htmx defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.dashboard :as dashboard :refer [gravatar]]
      [simpleui.launchpoint.web.views.profile.history :as profile.history]
      [simpleui.response :as response]))

(defcomponent item [req]
  [:div (pr-str (store/items req))])

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (if (-> req :session :id)
       (->> (assoc req :query-fn query-fn)
            item
            (page-htmx {:css ["/output.css"] :freeimage? true}))
       (response/redirect "/")))))
