(ns simpleui.launchpoint.util
    (:require
      [clojure.string :as string]
      [simpleui.launchpoint.env :refer [dev?]]))

(defn map-vals [f m]
  (->> m vals (map f) (zipmap (keys m))))

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

(defmacro defm [sym & rest]
  `(def ~sym (memoize (fn ~@rest))))

(defmacro defm-dev [sym & rest]
  `(def ~sym ((if dev? identity memoize) (fn ~@rest))))

(defn format$ [x]
  (format "%.2f" x))

(defn map-last [f s]
  (let [c (dec (count s))]
    (map-indexed #(f (= %1 c) %2) s)))

(defmacro defcss
  "Define tailwind classes for the tailwind parser (noop)"
  [& forms])
