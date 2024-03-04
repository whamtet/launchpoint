(ns simpleui.launchpoint.web.controllers.item-order)

(defn basket-count [{:keys [query-fn session]}]
  (:items (query-fn :basket-count session)))
