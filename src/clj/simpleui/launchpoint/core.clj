(ns simpleui.launchpoint.core
  (:require
    [clojurewerkz.quartzite.scheduler :as qs]
    [clojurewerkz.quartzite.triggers :as t]
    [clojurewerkz.quartzite.jobs :as j]
    [clojurewerkz.quartzite.schedule.cron :as cron]

    [clojure.tools.logging :as log]
    [integrant.core :as ig]
    [simpleui.launchpoint.config :as config]
    [simpleui.launchpoint.env :refer [defaults]]
    [simpleui.launchpoint.tmp :as tmp]

    ;; Edges
    [kit.edge.server.undertow]
    [simpleui.launchpoint.web.handler]

    ;; Routes
    [simpleui.launchpoint.web.routes.api]

    [kit.edge.db.sql.conman]
    [simpleui.launchpoint.migratus]
    [simpleui.launchpoint.web.routes.ui]
    [kit.edge.utils.nrepl])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
  (reify Thread$UncaughtExceptionHandler
    (uncaughtException [_ thread ex]
      (log/error {:what :uncaught-exception
                  :exception ex
                  :where (str "Uncaught exception on" (.getName thread))}))))

(defonce system (atom nil))

(defn stop-app []
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (ig/halt!))
  (shutdown-agents))

(defn start-app [& [params]]
  ((or (:start params) (:start defaults) (fn [])))
  (->> (config/system-config (or (:opts params) (:opts defaults) {}))
       (ig/prep)
       (ig/init)
       (reset! system))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(j/defjob RestartJob
  [ctx]
  ;; stop
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (ig/halt!))

  (tmp/rm)
  ;; start
  ((or (:start defaults) (fn [])))
  (->> (config/system-config (or (:opts defaults) {}))
       (ig/prep)
       (ig/init)
       (reset! system)))

(defn schedule []
  (let [s   (-> (qs/initialize) qs/start)
        job (j/build
             (j/of-type RestartJob)
             (j/with-identity (j/key "jobs.noop.1")))
        trigger (t/build
                 (t/with-identity (t/key "triggers.1"))
                 (t/start-now)
                 (t/with-schedule (cron/schedule
                                   (cron/cron-schedule "0 0 0 * * ?"))))]
    (qs/schedule s job trigger)))

(defn -main [& _]
  (start-app)
  (schedule))
