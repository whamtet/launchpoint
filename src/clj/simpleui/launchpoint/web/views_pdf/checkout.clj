(ns simpleui.launchpoint.web.views-pdf.checkout
    (:require
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.controllers.item-order :as item-order]
      [simpleui.launchpoint.web.views-pdf.core :as core]
      [simpleui.launchpoint.util :refer [format$]]))

(defn- subtotal [orders]
  (->> orders
       (map #(* (:price %) (:quantity %)))
       (apply +)))

(defn- order-summary-row [{:keys [title price quantity]}]
  [[:cell title]
   [:cell (format$ (* price quantity))]])

(defn- final-row [orders]
  [[:cell.bold (i18n "Total")]
   [:cell.bold (format$ (subtotal orders))]])

(defn order-summary* [req]
  (let [items (item-order/order1 req)
        title (str (i18n "Order Summary") " " (-> req :path-params :order-id))]
    [{:title title
      :stylesheet core/stylesheet}
     (core/h1 title)
     (core/table
      (concat
       (map order-summary-row items)
       (list (final-row items))))]))

(defn order-summary [req]
  (core/pdf (order-summary* req)))
