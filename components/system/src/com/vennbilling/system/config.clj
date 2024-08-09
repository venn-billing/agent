(ns com.vennbilling.system.config
  (:require
    [aero.core :as aero]
    [integrant.core :as ig]))


(defmethod aero.core/reader 'ig/ref
  [_ _ value]
  (ig/ref value))


(defmethod aero.core/reader 'ig/refset
  [_ _ value]
  (ig/refset value))


(defmethod ig/init-key :system/env [_ env] env)


(defn read-config
  [config-file opts]
  (aero/read-config config-file opts))
