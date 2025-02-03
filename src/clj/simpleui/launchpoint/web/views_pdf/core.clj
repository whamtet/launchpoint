(ns simpleui.launchpoint.web.views-pdf.core
  (:require
    [clj-pdf.core :as pdf])
  (:import
    java.io.ByteArrayInputStream
    java.io.ByteArrayOutputStream))

(defn pdf [data]
  (let [out (ByteArrayOutputStream.)]
    (pdf/pdf data out)
    (-> out .toByteArray ByteArrayInputStream.)))

(defn break [size]
  [:paragraph {:size size} " "])

(def stylesheet
  {:gray {:color [63 63 70]}})
