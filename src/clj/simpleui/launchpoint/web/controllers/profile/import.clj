(ns simpleui.launchpoint.web.controllers.profile.import
    (:require
      [clojure.java.shell :refer [sh]])
    (:import
      java.io.File))

(defn slurp-pdf [{:keys [tempfile content-type]}]
  (assert (= content-type "application/pdf"))
  (sh "pdftotext" (str tempfile))
  (-> tempfile (str ".txt") slurp))
