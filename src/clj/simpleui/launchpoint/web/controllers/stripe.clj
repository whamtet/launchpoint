(ns simpleui.launchpoint.web.controllers.stripe
    (:import
      com.stripe.Stripe
      com.stripe.model.PaymentIntent))

(defn client-secret []
  (set! Stripe/apiKey (System/getenv "STRIPE_KEY"))
  (-> {"amount" 500
       "currency" "usd"}
      PaymentIntent/create
      .getClientSecret))
