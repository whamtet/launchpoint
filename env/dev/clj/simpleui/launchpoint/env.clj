(ns simpleui.launchpoint.env
  (:require
    [clojure.tools.logging :as log]
    [simpleui.launchpoint.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[launchpoint starting using the development or test profile]=-"))
   :start      (fn []
                 (log/info "\n-=[launchpoint started successfully using the development or test profile]=-"))
   :stop       (fn []
                 (log/info "\n-=[launchpoint has shut down successfully]=-"))
   :middleware wrap-dev
   :opts       {:profile       :dev
                :persist-data? true}})

(def dev? true)
(def prod? false)

(defn host [& strs]
  (apply str "http://localhost:3000" strs))
