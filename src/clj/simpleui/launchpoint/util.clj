(ns simpleui.launchpoint.util)

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
