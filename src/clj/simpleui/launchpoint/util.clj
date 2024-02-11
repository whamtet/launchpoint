(ns simpleui.launchpoint.util)

(defmacro zipm [& syms]
  (zipmap (map keyword syms) syms))
