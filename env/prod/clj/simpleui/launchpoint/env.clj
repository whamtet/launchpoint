(ns simpleui.launchpoint.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[launchpoint starting]=-"))
   :start      (fn []
                 (log/info "\n-=[launchpoint started successfully]=-"))
   :stop       (fn []
                 (log/info "\n-=[launchpoint has shut down successfully]=-"))
   :middleware (fn [handler _] handler)
   :opts       {:profile :prod}})

(def dev? false)
(def prod? true)
