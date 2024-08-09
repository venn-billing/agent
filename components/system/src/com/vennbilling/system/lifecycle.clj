(ns com.vennbilling.system.lifecycle
  (:require
    [clojure.java.io :as io]
    [com.vennbilling.system.config :as c]
    [integrant.core :as ig]))


(defonce  system (atom nil))

(def ^:private default-config-file (io/resource "system/default.edn"))
(def ^:private default-opts {:profile :dev})


(defn- valid-profile?
  [profile]
  (contains? {:prod true
              :dev :true} profile))


(defn- stop
  []
  (some-> (deref system) (ig/halt!)))


(defn start
  [banner config-file opts]

  (when banner
    (println "\n" banner))

  (let [f (or config-file default-config-file)
        profile (:profile opts)
        o (if (valid-profile? profile)
            opts
            default-opts)]
    (->> (c/read-config f o)
         (ig/prep)
         (ig/init)
         (reset! system)))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop)))
