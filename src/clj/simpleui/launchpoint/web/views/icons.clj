(ns simpleui.launchpoint.web.views.icons)

(defn- hero-icon [& paths]
  [:xmlns.http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg/svg
   {:fill "none",
    :viewBox "0 0 24 24",
    :stroke-width "1.5",
    :stroke "currentColor",
    :class "w-6 h-6"}
   (for [d paths]
     [:xmlns.http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg/path
      {:stroke-linecap "round",
       :stroke-linejoin "round",
       :d d}])])

;; tw-ignore
(defmacro deficon [sym & paths]
  `(def ~sym (hero-icon ~@paths)))
;; tw-ignore

(deficon star
  "M11.48 3.499a.562.562 0 0 1 1.04 0l2.125 5.111a.563.563 0 0 0 .475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 0 0-.182.557l1.285 5.385a.562.562 0 0 1-.84.61l-4.725-2.885a.562.562 0 0 0-.586 0L6.982 20.54a.562.562 0 0 1-.84-.61l1.285-5.386a.562.562 0 0 0-.182-.557l-4.204-3.602a.562.562 0 0 1 .321-.988l5.518-.442a.563.563 0 0 0 .475-.345L11.48 3.5Z")
