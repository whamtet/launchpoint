(ns simpleui.launchpoint.tmp
    (:import
      java.io.File))

(defn rm []
  (.delete (File. "launchpoint_dev.db"))
  (doseq [f (.listFiles (File. "receipts"))]
    (.delete f)))

(defn rm-receipt [order_id]
  (.delete
    (File. (format "receipts/%s.pdf" order_id))))
