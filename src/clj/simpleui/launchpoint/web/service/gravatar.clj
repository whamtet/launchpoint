(ns simpleui.launchpoint.web.service.gravatar
    (:require
      [clj-commons.digest :as digest]
      [clojure.core.cache.wrapped :as cache]
      [clojure.java.io :as io]
      [clj-http.client :as client]
      [simpleui.launchpoint.util :refer [defm]])
    (:import
      javax.imageio.ImageIO
      java.io.ByteArrayInputStream
      java.io.ByteArrayOutputStream))

(def cache (cache/lu-cache-factory {}))
; (cache/through-cache cache :hi prn)

(defn- gravatar-link [^String email size]
  (format "https://gravatar.com/avatar/%s?s=%s"
          (-> email .trim .toLowerCase digest/sha256)
          size))

(defn slurp-gravatar [email]
  (->
   email
   (gravatar-link 256)
   (client/get {:as :byte-array})
   :body))

(defn ->img [bytes]
  (-> bytes
      ByteArrayInputStream.
      ImageIO/read
      .getRaster
      .getDataBuffer
      .getData))

(defn mse* [pixels1 pixels2]
  (assert (= (alength pixels1) (alength pixels2)))
  (as-> (map #(* (- %1 %2) (- %1 %2)) pixels1 pixels2) $
        (apply + $)
        (/ $ (alength pixels1))
        (Math/sqrt $)))

(defm default-gravatar []
  (-> "asdf" slurp-gravatar ->img))

(defn mse [bytes]
  (mse* (->img bytes) (default-gravatar)))

(defm substitute-default []
  (let [o (ByteArrayOutputStream.)]
    (-> "profile-default.png" io/resource io/input-stream (io/copy o))
    (.toByteArray o)))

(defn- gravatar* [email]
  (let [pic (slurp-gravatar email)]
    (if (< (mse pic) 5)
      {:body (substitute-default)
       :mime "image/png"}
      {:body pic
       :mime "image/jpeg"})))

(defn gravatar [email]
  (-> cache
      (cache/through-cache email gravatar*)
      (get email)
      (update :body #(ByteArrayInputStream. %))))

(defn gravatar-raw [email]
  (-> cache
      (cache/through-cache email gravatar*)
      (get email)
      :body))
