;; no longer used - reference only.
(ns simpleui.launchpoint.web.controllers.pdf
    (:require
        [clj-http.client :as client]
        [clojure.data.json :as json]
        [clojure.java.io :as io]
        [simpleui.launchpoint.env :refer [prod?]]
        [simpleui.launchpoint.web.controllers.item-order :as item-order])
    (:import
      java.io.File))

(.mkdir (File. "receipts"))

(defn- ->multipart [m]
    (mapv
     (fn [[k v]]
         {:name (name k) :content v})
     m))

;; prod
;; docker run --rm --net=host -d gotenberg/gotenberg:8

;; dev
;; docker run --rm -d -p 3000:3000 gotenberg/gotenberg:8
(defn pdf-profile [cookie user-id]
    (:body
        (client/post "http://localhost:3000/forms/chromium/convert/url"
                     {:multipart
                      (->multipart
                       {:url (format "http://%s:3001/api/profile%s"
                                     (if prod? "localhost" "host.docker.internal")
                                     (if user-id
                                       (str "/" user-id)
                                       ""))
                        :extraHttpHeaders (json/write-str {:cookie cookie})})
                      :as :stream})))

(defn- pdf-order* [f cookie order-id]
  (->
    (client/post "http://localhost:3000/forms/chromium/convert/url"
                 {:multipart
                  (->multipart
                   {:url (format "http://%s:3001/api/order-raw/%s"
                                 (if prod? "localhost" "host.docker.internal")
                                 order-id)
                    :extraHttpHeaders (json/write-str {:cookie cookie})})
                  :as :stream})
   :body
   (io/copy f)))

(defn pdf-order [req cookie order-id]
  (item-order/order1-owner? req)
  (let [f (File. (format "receipts/%s.pdf" order-id))]
    (when-not (.exists f)
              (pdf-order* f cookie order-id))
    (io/input-stream f)))

