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
        first-column-lines (index-frequencies 0 0)]
    (when-let [[second-column-index frequency] (->> (dissoc index-frequencies 0)
                                                    (util/max-by second))]
      (when (-> first-column-lines (* 2/3) (<= frequency))
            second-column-index))))

(defn- truncator [i]
  #(if (> (.length %) i) (.substring % i) ""))

(defn- trim-page [page]
  (if-let [index (second-column-index page)]
    (map (truncator index) page)
    page))

(def months
  [nil
   "January"
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
   "December"])
(def months-piped
  (->> months rest (string/join "|")))
(def month-value
  (->> months
       (map-indexed (fn [i x] [x i]))
       (into {})))

(def date-range
  (re-pattern
   (format "(%s)? ?(\\d{4}) - (%s)? ?(\\d{4})" months-piped months-piped)))

(def date-range-education #"\d{4} - (\d{4})")

(defn- parse-description [lines]
  (let [s (->> lines (string/join "\n") .trim)
        [paragraph & rest] (.split s "\n\n")]
    (if rest
      (->> rest (map #(.trim %)) (string/join "\n\n"))
      paragraph)))

(defn- get-jobs [lines]
  (let [experience (->> lines
                        (map #(.trim %))
                        (drop-while #(not= "Experience" %))
                        (take-while #(not= "Education" %))
                        vec)]
    (->> experience
         (keep-indexed
          (fn [i line]
            (when (re-find date-range line) i)))
         (partition-all 2 1)
         (map
          (fn [[i j]]
            (let [[_ from-month from-year to-month to-year] (->> i experience (re-find date-range))
                  description-lines
                  (if j
                    (subvec experience (+ i 1) (- j 2))
                    (subvec experience (+ i 1)))]
              {:company (-> i (- 2) experience)
               :title (-> i dec experience)
               :from-month (month-value from-month)
               :from-year (Long/parseLong from-year)
               :to-month (month-value to-month)
               :to-year (Long/parseLong to-year)
               :description (parse-description description-lines)}))))))

(defn- get-education [lines]
  (let [education (->> lines
                       (map #(.trim %))
                       (drop-while #(not= "Education" %))
                       vec)]
    (->> education
         (keep-indexed
          (fn [i line]
            (when (re-find date-range-education line) i)))
         (map
          (fn [i]
            (let [[degree date-range] (-> i education (.split " · "))
                  [_ year] (re-find date-range-education date-range)]
              {:degree degree
               :institution (-> i dec education)
               :year (Long/parseLong year)}))))))

(defn- get-description [lines]
  (->> lines
       (map #(.trim %))
       (drop-while #(not= "Summary" %))
       rest
       (take-while #(not= "Experience" %))
       (filter not-empty)
       (string/join " ")))

(defn gen-cv [file]
  (let [all-lines (-> file
                      slurp-pdf
                      (.split "\n")
                      (->>
                       (partition-by-true page-end?)
                       (mapcat trim-page)))]
    {:jobs (get-jobs all-lines)
     :education (get-education all-lines)
     :description (get-description all-lines)}))
