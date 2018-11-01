(ns kotyo.async-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :as a]
            [kotyo.async]))

(deftest normal-message-passing
  (let [ch (a/chan 1)]
    (a/>!! ch :hi)
    (is (= :hi (a/<?? ch)))))

(deftest exception-message-passing
  (let [ch (a/chan 1)]
    (a/>!! ch (Exception. "Oh-crap!"))
    (is (thrown? Exception (a/<?? ch)))))

(deftest go-guard-exception
  (let [err-ch (a/chan 1)]
    (a/go-guard [err-ch]
                (throw (Exception. "Oh-no!")))
    (is (thrown? Exception (a/<?? err-ch)))))

(deftest go-guard-normal
  (let [ch (a/chan 1)]
    (a/go-guard [] (dotimes [i 10] (a/>! ch i)))
    (is (= 0 (a/<?? ch)))))

(deftest thread-normal
  (let [ch (a/chan 1)]
    (a/thread (dotimes [i 10] (a/>!! ch i)))
    (is (= 0 (a/<?? ch)))))

(deftest go-normal
  (let [ch (a/chan 1)]
    (a/go (dotimes [i 10] (a/>! ch i)))
    (is (= 0 (a/<?? ch)))))

(deftest go-guard-without-err-vector-normal
  (is (= 13
         (a/<?? (a/go-guard 13)))))

(deftest go-guard-without-err-vector-exception
  (is (thrown? Exception
               (a/<?? (a/go-guard (throw (Exception. "Oops!")))))))
