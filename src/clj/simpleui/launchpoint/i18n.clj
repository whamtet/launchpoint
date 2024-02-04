(ns simpleui.launchpoint.i18n)

(def phrases #{})

(defmacro i18n [^String s]
  (assert (= s (.trim s)))
  (alter-var-root #'phrases conj s)
  s)
