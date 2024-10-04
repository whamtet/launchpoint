(ns simpleui.launchpoint.web.service.gravatar
    (:require
      [clj-commons.digest :as digest]
      [clojure.core.cache.wrapped :as cache]
      [clojure.java.io :as io]
      [clj-http.client :as client]
      [simpleui.launchpoint.util :refer [defm]])
    (:import
      java.io.ByteArrayInputStream
      java.io.ByteArrayOutputStream
      java.util.Arrays))

(def cache (cache/lu-cache-factory {}))
; (cache/through-cache cache :hi prn)

(defn- gravatar-link [^String email size]
  (format "https://gravatar.com/avatar/%s?s=%s"
          (-> email .trim .toLowerCase digest/sha256)
          size))

(defn slurp-gravatar [email]
  (prn (gravatar-link email 256))
  (->
   email
   (gravatar-link 256)
   (client/get {:as :byte-array})
   :body))

(defm default-gravatar []
  (slurp-gravatar "asdf"))

(defm substitute-default []
  (let [o (ByteArrayOutputStream.)]
    (-> "profile-default.png" io/resource io/input-stream (io/copy o))
    (.toByteArray o)))

(defn- gravatar* [email]
  (let [pic (slurp-gravatar email)]
    (if (Arrays/equals pic (default-gravatar))
      {:body (substitute-default)
       :mime "image/png"}
      {:body pic
       :mime "image/jpeg"})))

(defn gravatar [email]
  (-> email
      gravatar*
      (update :body #(ByteArrayInputStream. %))) #_
  (-> cache
      (cache/through-cache email gravatar*)
      (get email)
      (update :body #(ByteArrayInputStream. %))))
