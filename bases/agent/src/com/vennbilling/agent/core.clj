(ns com.vennbilling.agent.core
  (:gen-class)
  (:require
    [aero.core :refer [read-config]]
    [clojure.java.io :as io]
    [com.vennbilling.customer.interface :as customer]
    [com.vennbilling.healthcheck.interface :as healthcheck]
    [com.vennbilling.spec.interface :as venn-spec]
    [integrant.core :as ig]
    [io.pedestal.log :as log]
    [muuntaja.core :as m]
    [reitit.coercion.malli :as malli]
    [reitit.ring :as ring]
    [reitit.ring.coercion :as coercion]
    [reitit.ring.middleware.exception :as exception]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [ring.adapter.undertow :refer [run-undertow]]
    [ring.logger :as logger]))


(defmethod aero.core/reader 'ig/ref
  [_ _ value]
  (ig/ref value))


(defmethod aero.core/reader 'ig/refset
  [_ _ value]
  (ig/refset value))


(defmethod ig/init-key :system/env [_ env] env)


(defmethod ig/init-key :server/http
  [_ {:keys [db] :as opts}]
  (let [handler (atom (delay (:handler opts)))]
    {:handler handler}
    {:server (run-undertow (fn [req] (@@handler (assoc req :db db))) (dissoc opts :handler))}))


(defmethod ig/halt-key! :server/http
  [_ {:keys [server]}]
  (.stop server))


(defmethod ig/init-key :handler/ring
  [_ {:keys [router]}]
  (logger/wrap-with-logger
    (ring/ring-handler
      router
      (ring/create-default-handler))))


(defn internal-routes
  [_opts]
  [healthcheck/simple-route])


(defn api-routes
  [_opts]
  [venn-spec/identify-route
   customer/list-route
   customer/show-route])


(defn route-data
  [opts]
  (merge
    opts
    {:coercion malli/coercion
     :muuntaja m/instance
     :middleware [;; query-params & form-params
                  parameters/parameters-middleware
                  ;; content-negotiation
                  muuntaja/format-negotiate-middleware
                  ;; encoding response body
                  muuntaja/format-response-middleware
                  ;; exception handling
                  exception/exception-middleware
                  ;; decoding request body
                  muuntaja/format-request-middleware
                  ;; coercing response bodys
                  coercion/coerce-response-middleware
                  ;; coercing request parameters
                  coercion/coerce-request-middleware]}))


(derive :agent.routes/internal :agent/routes)
(derive :agent.routes/api :agent/routes)


(defmethod ig/init-key :agent.routes/api
  [_ {:keys [base-path]
      :as opts}]
  [base-path (dissoc (route-data opts) :base-path) (api-routes opts)])


(defmethod ig/init-key :agent.routes/internal
  [_ {:keys [base-path]
      :as opts}]
  [base-path (dissoc (route-data opts) :base-path) (internal-routes opts)])


(defmethod ig/init-key :router/routes
  [_ {:keys [routes]}]
  (apply conj [] routes))


(defmethod ig/init-key :router/core
  [_ {:keys [routes] :as opts}]
  (ring/router ["" opts routes]))


(defonce system (atom nil))
(def ^:const banner (slurp (io/resource "agent/banner.txt")))


(defn- config
  [opts]
  (read-config (io/resource "agent/system.edn") opts))


(defn stop-app
  []
  (log/info :msg "venn agent has shut down successfully")
  (some-> (deref system) (ig/halt!)))


(defn start-app
  [{:keys [profile] :as opts}]

  (System/setProperty "org.jboss.logging.provider" "slf4j")

  (println "\n" banner)
  (log/info :msg "venn agent started successfully." :profile profile)

  (->> (config opts)
       (ig/prep)
       (ig/init)
       (reset! system))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))


(defn -main
  [& _]
  (start-app {:profile :prod}))
