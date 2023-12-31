(ns venn.agent.core
  (:require [integrant.core :as ig]

            [venn.agent.config :refer [config]]
            [venn.agent.env :refer [defaults]]
            [venn.agent.db.xtdb]
            [venn.agent.http.routes]
            [venn.agent.http.server])
  (:gen-class))

(defonce system (atom nil))

(defn stop-app []
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (ig/halt!)))

(defn start-app []
  ((or (:start defaults) (fn [])))
  (->> (config (:opts defaults))
       (ig/prep)
       (ig/init)
       (reset! system))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& _]
  (start-app))
