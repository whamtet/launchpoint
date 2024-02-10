(ns simpleui.launchpoint.i18n)

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
