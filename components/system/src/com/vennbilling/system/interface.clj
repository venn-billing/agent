(ns com.vennbilling.system.interface
  (:require
    [com.vennbilling.system.lifecycle :as lifecycle]))


(defn start
  [banner config-file opts]
  (lifecycle/start banner config-file opts))
