(ns simpleui.launchpoint.web.controllers.profile.import
    (:require
      [clojure.java.shell :refer [sh]]
      [clojure.string :as string]
      [simpleui.launchpoint.util :as util])
    (:import
      java.io.File))

(defn slurp-pdf [{:keys [tempfile content-type]}]
  (assert (= content-type "application/pdf"))
  (sh "pdftotext" "-layout" (str tempfile))
  (-> tempfile (str ".txt") slurp))

(defn- partition-by-true [f s]
  (->> s (partition-by f) (take-nth 2)))

(def s (slurp "profile.txt"))
(use 'clojure.pprint)

(defn- page-end? [s]
  (boolean
   (re-find #"Page \d+ of \d+" s)))

(defn- capital-indices [line]
  (keep-indexed
   (fn [i char]
     (when (re-find #"[A-Z]" (str char)) i))
   line))

(defn- second-column-index [lines]
  (let [index-frequencies (->> lines (mapcat capital-indices) frequencies)
        first-column-lines (index-frequencies 0 0)
        [second-column-index frequency] (->> (dissoc index-frequencies 0)
                                             (apply max-key second))]
    (when (-> first-column-lines (* 2/3) (<= frequency))
          second-column-index)))

(defn- truncator [i]
  #(if (> (.length %) i) (.substring % i) ""))

(defn- trim-page [page]
  (if-let [index (second-column-index page)]
    (map (truncator index) page)
    page))

(def months-piped
  (string/join "|"
               ["January"
                "February"
                "March"
                "April"
                "May"
                "June"
                "July"
                "August"
                "September"
                "October"
                "November"
                "December"]))

(def date-range
  (re-pattern
   (format "(%s)? ?(\\d{4}) - (%s)? ?(\\d{4})" months-piped months-piped)))

(defn gen-cv [s]
  (let [pages (partition-by-true page-end? (.split s "\n"))
        all-lines (mapcat trim-page pages)]
    (keep #(re-find date-range %) all-lines)))

(pprint
 (gen-cv s))
