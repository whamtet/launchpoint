(ns simpleui.launchpoint.web.routes.api
  (:require
    [simpleui.launchpoint.env :refer [dev?]]
    [simpleui.launchpoint.web.controllers.delete :as delete]
    [simpleui.launchpoint.web.controllers.health :as health]
    [simpleui.launchpoint.web.controllers.item-order :as item-order]
    [simpleui.launchpoint.web.controllers.login :as login]
    [simpleui.launchpoint.web.controllers.pdf :as pdf]
    [simpleui.launchpoint.web.controllers.stripe :as stripe]
    [simpleui.launchpoint.web.htmx :refer [page-simple redirect-tab]]
    [simpleui.launchpoint.web.middleware.exception :as exception]
    [simpleui.launchpoint.web.middleware.formats :as formats]
    [simpleui.launchpoint.web.service.gravatar :as gravatar]
    [simpleui.launchpoint.web.views.checkout :as views.checkout]
    [simpleui.launchpoint.web.views.user :as views.user]
    [simpleui.launchpoint.web.views-pdf.user :as views-pdf.user]
    [integrant.core :as ig]
    [reitit.coercion.malli :as malli]
    [reitit.ring.coercion :as coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.swagger :as swagger]))

(defn- redirect [location]
  {:status 302, :headers {"Location" location}, :body ""})

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
  (cond->
   [["/swagger.json"
     {:get {:no-doc  true
            :swagger {:info {:title "simpleui.launchpoint API"
                             :description "This isn't a true API.  See https://simpleui.io"}}
            :handler (swagger/create-swagger-handler)}}]
    ;; stripe token
    ["/stripe"
     (fn [{:keys [session]}]
       (-> session :id assert)
       {:status 200
        :headers {}
        :body (stripe/client-secret)})]
    ;; create a new order
    ["/checkout"
     (fn [req]
       (-> req :session :id assert)
       (redirect
        (if-let [order-id (-> req (assoc :query-fn query-fn) item-order/complete-order)]
          (str "/api/checkout/complete/" order-id)
          "/")))]
    ["/checkout/complete/:order-id"
     (fn [req]
       (redirect-tab
        (->> req :path-params :order-id (str "/api/order/"))
        "/"))]
    ["/logout"
     (fn [{:keys [session]}]
       (login/logout session))]
    ;; self profile raw html
    ["/profile"
     (fn [req]
       (page-simple {:css ["/output.css"]}
                    (views.user/profile (assoc req :query-fn query-fn) true)))]
    ["/profile/:user-id"
     (fn [req]
       (page-simple {:css ["/output.css"]}
                    (views.user/profile (assoc req :query-fn query-fn) true)))]
    ["/order-raw/:order-id"
     (fn [req]
       (page-simple {:css ["/output.css"]}
                    (views.checkout/order-summary (assoc req :query-fn query-fn))))]
    ["/order/:order-id"
     (fn [{:keys [session headers path-params] :as req}]
       (assert (:id session))
       {:status 200
        :headers {"Content-Type" "application/pdf"}
        :body (-> req
                  (assoc :query-fn query-fn)
                  (pdf/pdf-order (headers "cookie") (:order-id path-params)))})]
    ["/profile-pdf"
     (fn [req]
       (-> req :session :id assert)
       {:status 200
        :headers {"Content-Type" "application/pdf"}
        :body (-> req (assoc :query-fn query-fn) views-pdf.user/profile)})]
    ["/profile-pdf/:user-id"
     (fn [{:keys [session headers path-params]}]
       (assert (:id session))
       {:status 200
        :headers {"Content-Type" "application/pdf"}
        :body (pdf/pdf-profile (headers "cookie") (:user-id path-params))})]
    ["/gravatar/:email"
     (fn [{:keys [session path-params]}]
       (assert (:id session))
       (let [{:keys [body mime]} (-> path-params :email gravatar/gravatar)]
         {:status 200
          :headers {"Content-Type" mime}
          :body body}))]
    ["/inspect"
     (fn [req]
       {:status 200
        :headers {"Content-Type" "text/html"}
        :body (-> req (dissoc :reitit.core/match) pr-str)})]
    ["/health"
     {:get health/healthcheck!}]]
   dev? (conj ["/user/:user-id"
               (fn [req]
                 (-> req (assoc :query-fn query-fn) delete/delete-user)
                 {:status 200
                  :headers {"Content-Type" "text/html"}
                  :body "ok"})])))

(derive :reitit.routes/api :reitit/routes)

(defmethod ig/init-key :reitit.routes/api
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  [base-path route-data (api-routes opts)])
