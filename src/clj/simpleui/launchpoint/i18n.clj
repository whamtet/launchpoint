(ns simpleui.launchpoint.i18n
    (:require
      [clojure.data.json :as json]
      [clojure.java.io :as io]
      [clojure.string :as string]
      [simpleui.launchpoint.web.middleware.i18n :as middleware.i18n]
      [simpleui.launchpoint.util :as util :refer [defm-dev]]))

(defn- zip-pairs [s]
  (zipmap
   (take-nth 2 s)
   (take-nth 2 (rest s))))

(defn slurp-translation [lang]
  (->> lang
       (format "i18n/%s.txt")
       io/resource
       slurp))

(defm-dev extract-translation [lang]
  (if (and lang (not= "en" lang))
    (-> lang
        slurp-translation
        (.split "\n")
        (->>
         (map #(.trim %))
         (filter not-empty)
         zip-pairs))
    {}))

(def phrases #{})

(defn- trimmed? [^String s]
  (= s (.trim s)))

(defmacro i18n [^String s]
  (assert (trimmed? s))
  (alter-var-root #'phrases conj s)
  `((extract-translation middleware.i18n/*lang*) ~s ~s))

(defmacro i18ns [& ss]
  (assert (every? trimmed? ss))
  (alter-var-root #'phrases #(apply conj % ss))
  `(let [m# (extract-translation middleware.i18n/*lang*)]
    (mapv #(m# % %) (list ~@ss))))

(defmacro i18n-map [m]
  (assert (map? m))
  (let [new-phrases (vals m)]
    (assert (every? trimmed? new-phrases))
    (alter-var-root #'phrases #(apply conj % new-phrases))
    `(let [m# (extract-translation middleware.i18n/*lang*)]
      (json/write-str
       (util/map-vals #(m# % %) ~m)))))

(defn spit-phrases []
  (->> phrases
       sort
       (map #(str % "\n"))
       (string/join "\n")
       (spit "i18n.txt")))
