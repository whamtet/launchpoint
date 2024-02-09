(ns simpleui.launchpoint.web.routes.api
  (:require
    [clojure.java.io :as io]
    [simpleui.launchpoint.web.controllers.health :as health]
    [simpleui.launchpoint.web.controllers.login :as login]
    [simpleui.launchpoint.web.middleware.exception :as exception]
    [simpleui.launchpoint.web.middleware.formats :as formats]
    [integrant.core :as ig]
    [reitit.coercion.malli :as malli]
    [reitit.ring.coercion :as coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.swagger :as swagger]))

(def route-data
  {:coercion   malli/coercion
   :muuntaja   formats/instance
   :swagger    {:id ::api}
   :middleware [;; query-params & form-params
                parameters/parameters-middleware
                  ;; content-negotiation
                muuntaja/format-negotiate-middleware
                  ;; encoding response body
                muuntaja/format-response-middleware
                  ;; exception handling
                coercion/coerce-exceptions-middleware
                  ;; decoding request body
                muuntaja/format-request-middleware
                  ;; coercing response bodys
                coercion/coerce-response-middleware
                  ;; coercing request parameters
                coercion/coerce-request-middleware
                  ;; exception handling
                exception/wrap-exception]})

;; Routes
(defn api-routes [_opts]
  [["/swagger.json"
    {:get {:no-doc  true
           :swagger {:info {:title "simpleui.launchpoint API"}}
           :handler (swagger/create-swagger-handler)}}]
   ["/logout"
    (fn [{:keys [session]}]
      (login/logout session))]
   ["/company/:src"
    (fn [{:keys [session path-params]}]
      (-> session :id assert)
      {:status 200
       :headers {}
       :body (->> path-params :src (str "logos/") io/input-stream)})]
   ["/health"
    {:get health/healthcheck!}]])

(derive :reitit.routes/api :reitit/routes)

(defmethod ig/init-key :reitit.routes/api
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  [base-path route-data (api-routes opts)])
