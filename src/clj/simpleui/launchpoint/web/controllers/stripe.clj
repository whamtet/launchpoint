(ns simpleui.launchpoint.web.controllers.stripe
    (:import
      com.stripe.Stripe
      com.stripe.model.PaymentIntent))

(defn client-secret []
  (set! Stripe/apiKey "sk_test_4eC39HqLyjWDarjtT1zdp7dc")
  (-> {"amount" 500
       "currency" "usd"}
      PaymentIntent/create
      .getClientSecret))
