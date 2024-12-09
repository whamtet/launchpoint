(ns simpleui.launchpoint.web.routes.ui
  (:require
   [simpleui.launchpoint.web.middleware.exception :as exception]
   [simpleui.launchpoint.web.middleware.formats :as formats]
   [simpleui.launchpoint.web.views.checkout :as checkout]
   [simpleui.launchpoint.web.views.home :as home]
   [simpleui.launchpoint.web.views.item :as item]
   [simpleui.launchpoint.web.views.store :as store]
   [simpleui.launchpoint.web.views.preferences :as preferences]
   [simpleui.launchpoint.web.views.profile :as profile]
   [simpleui.launchpoint.web.views.user :as user]
   [integrant.core :as ig]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]))

(defn route-data [opts]
  (merge
   opts
   {:muuntaja   formats/instance
    :middleware
    [;; Default middleware for ui
    ;; query-params & form-params
      parameters/parameters-middleware
      ;; encoding response body
      muuntaja/format-response-middleware
      ;; exception handling
      exception/wrap-exception]}))

(derive :reitit.routes/ui :reitit/routes)

(defmethod ig/init-key :reitit.routes/ui
  [_ opts]
  [["" (route-data opts) (home/ui-routes opts)]
   ["/store" (route-data opts) (store/ui-routes opts)]
   ["/preferences" (route-data opts) (preferences/ui-routes opts)]
   ["/profile" (route-data opts) (profile/ui-routes opts)]
   ["/user/:user-id" (route-data opts) (user/ui-routes opts)]
   ["/item/:item-id" (route-data opts) (item/ui-routes opts)]
   ["/checkout" (route-data opts) (checkout/ui-routes opts)]])
