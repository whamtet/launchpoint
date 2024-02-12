(ns simpleui.launchpoint.web.controllers.pdf
    (:require
        [clj-http.client :as client]
        [clojure.data.json :as json]))

(defn- ->multipart [m]
    (mapv
     (fn [[k v]]
         {:name (name k) :content v})
     m))

(defn pdf-profile [cookie]
    (:body
        (client/post "http://localhost:3001/forms/chromium/convert/url"
                     {:multipart
                      (->multipart
                       {:url "http://localhost:3000/api/profile"
                        :extraHttpHeaders (json/write-str {:cookie cookie})})
                      :as :stream})))
