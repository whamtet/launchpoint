(ns simpleui.launchpoint.pdf
  (:require
    [clj-pdf.core :as pdf])
  (:import
    java.io.ByteArrayInputStream
    java.io.ByteArrayOutputStream))

(defn pdf [data]
  (let [out (ByteArrayOutputStream.)]
    (pdf/pdf data out)
    (-> out .toByteArray ByteArrayInputStream.)))
