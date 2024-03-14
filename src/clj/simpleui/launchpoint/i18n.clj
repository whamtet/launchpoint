(ns simpleui.launchpoint.i18n
    (:require
      [clojure.data.json :as json]
      [clojure.string :as string]))

(def phrases #{})

(defn- trimmed? [^String s]
  (= s (.trim s)))

(defmacro i18n [^String s]
  (assert (trimmed? s))
  (alter-var-root #'phrases conj s)
  s)

(defmacro i18ns [& ss]
  (assert (every? trimmed? ss))
  (alter-var-root #'phrases #(apply conj % ss))
  (vec ss))

(defmacro i18n-map [m]
  (assert (map? m))
  (let [new-phrases (vals m)]
    (assert (every? trimmed? new-phrases))
    (alter-var-root #'phrases #(apply conj % new-phrases))
    (json/write-str m)))

(defn spit-phrases []
  (->> phrases
       sort
       (map #(str % "\n"))
       (string/join "\n")
       (spit "i18n.txt")))
