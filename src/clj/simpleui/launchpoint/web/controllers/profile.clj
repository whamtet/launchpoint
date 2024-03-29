(ns simpleui.launchpoint.web.controllers.profile
    (:require
      [simpleui.launchpoint.util :as util]
      [simpleui.launchpoint.web.controllers.company :as company]
      [simpleui.launchpoint.web.controllers.profile.import :as import]))

(defn- upsert [{:keys [session query-fn]} cv]
  (query-fn :upsert-cv (assoc session :cv (pr-str cv))))

(defn get-cv [{:keys [session query-fn path-params]}]
  (let [params (if-let [user-id (:user-id path-params)]
                 {:id user-id}
                 session)]
    (some-> (query-fn :get-cv params)
            :cv
            read-string)))

(defn- update-cv [req f & args]
  (->> (apply f (get-cv req) args)
       (upsert req)))

(defn update-description [req description]
  (update-cv req assoc :description description))

(defn- date-value [{:keys [year from-year from-month]}]
  (cond
   (and from-year from-month) (+ (* 12 from-year) from-month)
   year year
   :else 0))

(defn- clean-job [{:keys [src company] :as job}]
  (if-let [src (company/companies company)]
    (assoc job :src src)
    (dissoc job :src)))

(defn- add-job_ [jobs i job]
  (let [jobs (if i (util/remove-i jobs i) jobs)
        job-value (date-value job)
        inserter #(< job-value (date-value %))]
    (util/insert-with inserter jobs (clean-job job))))

(defn add-job [req i job]
  (update-cv req update :jobs add-job_ i job))

(defn remove-job [req i]
  (update-cv req update :jobs util/remove-i i))

(defn add-education [req i education]
  (update-cv req update :education add-job_ i education))

(defn remove-education [req i]
  (update-cv req update :education util/remove-i i))

(defn import-profile [req file]
  (->> file import/gen-cv (upsert req)))
