(ns com.vennbilling.http.core
  (:require [ring.util.http-response :as http]))

(defn respond-with
  "Converts handler result tuples like [:ok data] into proper HTTP responses"
  [result]
  (let [[status body] result]
    (case status
      :ok (http/ok body)
      :created (http/created "" body)
      :no-content (http/no-content)
      :bad-request (http/bad-request body)
      :not-found (http/not-found body)
      (http/bad-request body))))

(defn defhandler
  "Defines a handler function that automatically applies respond-with to the result.
   
   Usage:
   (defhandler my-handler [request]
     [:ok {:message \"Hello\"}])
   
   This is equivalent to:
   (defn my-handler [request]
     (respond-with [:ok {:message \"Hello\"}]))"
  [name args & body]
  `(defn ~name ~args
     (respond-with (do ~@body))))
