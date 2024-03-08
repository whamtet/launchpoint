(ns simpleui.launchpoint.util
    (:require
      [clojure.string :as string]))

(defmacro zipm [& syms]
  (zipmap (map keyword syms) syms))

(defn insert-with [f s x]
  (concat
   (take-while f s)
   [x]
   (drop-while f s)))

(defn remove-i [s i]
  (concat
   (take i s)
   (drop (inc i) s)))

(defmacro deftrim [s args & body]
  (let [trims (vec (filter #(-> % meta :trim) args))]
    (assert (not-empty trims))
    `(defn ~s ~args
      (let [~trims (map (memfn trim) ~trims)]
        ~@body))))

(defmacro $format [s]
  (assert (string? s))
  `(-> ~s
    ~@(for [[to-replace replacement] (distinct (re-seq #"\{([^\}]+)}" s))]
       `(string/replace ~to-replace (str ~(read-string replacement))))))

(defn max-by [f s]
  (when (not-empty s)
        (apply max-key f s)))

(defmacro defm [sym args & body]
  `(def ~sym (memoize (fn ~args ~@body))))
