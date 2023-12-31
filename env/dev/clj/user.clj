(ns user
  (:require [aero.core :refer [read-config]]
            [clojure.tools.logging :as log]
            [clojure.tools.namespace.repl :as repl]
            [clojure.java.io]
            [integrant.core :as ig]
            [integrant.repl :refer [prep go halt reset]]
            [integrant.repl.state]

            [venn.agent.core :refer [start-app]]
            [venn.agent.http.server]
            [venn.agent.http.routes]))



(def config (read-config (clojure.java.io/resource "system.edn") {:profile :dev}))

(integrant.repl/set-prep! #(ig/prep config))
(repl/set-refresh-dirs "src/clj" "env/dev/clj")


;; Helpers to start and stop the system
(comment
  (prep)
  integrant.repl.state/config
  (go)
  (halt)
  (reset))
