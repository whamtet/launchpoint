(ns simpleui.launchpoint.web.routes.api
  (:require
    [clojure.java.io :as io]
    [simpleui.launchpoint.web.controllers.health :as health]
    [simpleui.launchpoint.web.controllers.login :as login]
    [simpleui.launchpoint.web.controllers.pdf :as pdf]
    [simpleui.launchpoint.web.controllers.stripe :as stripe]
    [simpleui.launchpoint.web.htmx :refer [page-simple]]
    [simpleui.launchpoint.web.middleware.exception :as exception]
    [simpleui.launchpoint.web.middleware.formats :as formats]
    [simpleui.launchpoint.web.views.profile :refer [profile-pdf]]
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
(defn api-routes [{:keys [query-fn]}]
  [["/swagger.json"
    {:get {:no-doc  true
           :swagger {:info {:title "simpleui.launchpoint API"}}
           :handler (swagger/create-swagger-handler)}}]
   ["/stripe"
    (fn [{:keys [session]}]
      (-> session :id assert)
      {:status 200
       :headers {}
       :body (stripe/client-secret)})]
   ["/logout"
    (fn [{:keys [session]}]
      (login/logout session))]
   ["/company/:src"
    (fn [{:keys [session path-params]}]
      (-> session :id assert)
      {:status 200
       :headers {}
       :body (->> path-params :src (str "logos/") io/input-stream)})]
   ["/profile"
    (fn [req]
      (page-simple {:css ["/output.css"]}
                   (profile-pdf (assoc req :query-fn query-fn))))]
   ["/profile/pdf"
    (fn [{:keys [session headers]}]
      (assert (:id session))
      {:status 200
       :headers {"Content-Type" "application/pdf"}
       :body (-> "cookie" headers pdf/pdf-profile)})]
   ["/health"
    {:get health/healthcheck!}]])

(derive :reitit.routes/api :reitit/routes)

(defmethod ig/init-key :reitit.routes/api
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  [base-path route-data (api-routes opts)])
