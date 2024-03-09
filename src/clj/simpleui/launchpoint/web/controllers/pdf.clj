(ns simpleui.launchpoint.web.controllers.pdf
    (:require
        [clj-http.client :as client]
        [clojure.data.json :as json]
        [simpleui.launchpoint.env :refer [prod?]]))

(defn- ->multipart [m]
    (mapv
     (fn [[k v]]
         {:name (name k) :content v})
     m))

;; docker run --rm --net=host -d gotenberg/gotenberg:8
;; docker run --rm -d -p 3000:3000 gotenberg/gotenberg:8
(defn pdf-profile [cookie]
    (:body
        (client/post "http://localhost:3000/forms/chromium/convert/url"
                     {:multipart
                      (->multipart
                       {:url (format "http://%s:3001/api/profile"
                                     (if prod? "localhost" "host.docker.internal"))
                        :extraHttpHeaders (json/write-str {:cookie cookie})})
                      :as :stream})))

(defn pdf-order [cookie order-id]
  (:body
    (client/post "http://localhost:3000/forms/chromium/convert/url"
                 {:multipart
                  (->multipart
                   {:url (format "http://%s:3001/api/order-raw/%s"
                                 (if prod? "localhost" "host.docker.internal")
                                 order-id)
                    :extraHttpHeaders (json/write-str {:cookie cookie})})
                  :as :stream})))
