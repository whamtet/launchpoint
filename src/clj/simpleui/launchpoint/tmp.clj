(ns simpleui.launchpoint.tmp
    (:import
      java.io.File))

(defn rm []
  (.delete (File. "launchpoint_dev.db"))
  (doseq [f (.listFiles (File. "receipts"))]
    (.delete f)))
