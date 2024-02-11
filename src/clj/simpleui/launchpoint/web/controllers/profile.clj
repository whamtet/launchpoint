(ns simpleui.launchpoint.web.controllers.profile
    (:require
      [simpleui.launchpoint.util :as util]
      [simpleui.launchpoint.web.controllers.company :as company]))

(defn- upsert [{:keys [session query-fn]} cv]
  (query-fn :upsert-cv (assoc session :cv (pr-str cv))))

(defn get-cv [{:keys [session query-fn]}]
  (some-> (query-fn :get-cv session)
          :cv
          read-string))

(defn- update-cv [req f & args]
  (->> (apply f (get-cv req) args)
       (upsert req)))

(defn update-description [req description]
  (update-cv req assoc :description description))

(defn- date-value [{:keys [from-year from-month]}]
  (if (and from-year from-month)
    (+ (* 12 from-year) from-month)
    0))

(defn- clean-job [{:keys [src company] :as job}]
  (if (company/company-match? src company)
    job
    (dissoc job :src)))
(defn add-job [req job]
  (let [job-value (date-value job)
        inserter #(< job-value (date-value %))]
  (update-cv req update :jobs #(util/insert-with inserter % (clean-job job)))))
