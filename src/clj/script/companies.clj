(ns script.companies
    (:require
      [clojure.java.io :as io]
      [clojure.data.json :as json])
    (:import
      java.io.File))

(def files (->> "scrape/companies"
                File.
                file-seq
                (filter #(-> % .getName (.endsWith "-")))))

(def ignore? #{"TTML.NS-95da7cf1.png"
               "ORIC-271f23cc.png"
               "OCN-b40106f6.png"
               "GRWG-6e94007f.png"
               "DX-1c9757d2.png"
               "AXL-b8d90789.png"
               "IFF-8fef9e9c.png"
               "3667.T-646a4543.png"})

(defn download1 [[url]]
  (let [url (-> url (.split "\\?") first)
        filename (last (.split url "/"))
        file (->> filename (str "logos/") File.)]
    (when-not (or (.exists file) (ignore? filename))
              (prn url)
              (with-open [in (io/input-stream url)]
                (io/copy in file)))))

(defn download [json]
  (let [data (-> json slurp json/read-str)]
    (dorun (pmap download1 data))))

(defn clean [[url company]]
  (let [filename (-> url (.split "\\?") first (.split "/") last)]
    (when-not (ignore? filename)
              [company filename])))

#_
(->> files
     (mapcat #(-> % slurp json/read-str))
     (keep clean)
     (into {})
     pr-str
     (spit "resources/companies.edn"))
