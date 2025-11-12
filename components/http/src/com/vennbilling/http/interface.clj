(ns com.vennbilling.http.interface
  (:require [com.vennbilling.http.core :as core]
            [com.vennbilling.http.ring :as ring]
            [com.vennbilling.http.routes :as routes]
            [com.vennbilling.http.undertow :as undertow]))

(def healthcheck-routes [routes/healthchecks])

(def agent-routes [routes/spec])

(def server-routes
  [routes/customers])

(defmacro defhandler
  "Defines a handler function that automatically applies respond-with to the result.
   See com.vennbilling.http.core/defhandler for implementation details."
  [name args & body]
  `(core/defhandler ~name ~args ~@body))

(defn serve
  "Starts up an HTTP server with a io.undertow config and ring handler. Returns an undertow server"
  [config handler]
  (undertow/serve config handler))

(defn new-ring-handler
  "Returns a ring handler function that wraps a ring router and can be used with an HTTP server."
  ([router]
   (ring/new-handler router))

  ([router storage]
   (ring/new-handler router storage)))

(defn new-ring-router
  "Returns a new ring router that can be used with an ring handler"
  ([routes]
   (new-ring-router "" routes []))
  ([routes middleware]
   (new-ring-router "" routes middleware))
  ([base-path routes middleware]
   (ring/new-router base-path routes middleware)))
