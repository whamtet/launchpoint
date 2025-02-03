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
  {:gray {:color [63 63 70]}
   :bold {:style :bold}})

(defn h1 [& content]
  [:paragraph {:size 20} content])

(defn table [rows]
  (apply conj [:table {:border false :cell-border false}] rows))
