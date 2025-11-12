(ns com.vennbilling.http.interface-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [com.vennbilling.http.interface :refer [defhandler]]))

(defhandler test-handler [_request]
  [:ok {:message "test"}])

(defhandler test-created-handler [_request]
  [:created {:id 123}])

(deftest defhandler-macro-test
  (testing "defhandler macro creates proper HTTP responses"
    (testing "ok response"
      (let [response (test-handler {})]
        (is (= 200 (:status response)))
        (is (= {:message "test"} (:body response)))))

    (testing "created response"
      (let [response (test-created-handler {})]
        (is (= 201 (:status response)))
        (is (= {:id 123} (:body response)))))))
