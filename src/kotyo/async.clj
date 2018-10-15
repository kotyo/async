(ns kotyo.async
  (:require [clojure.core.async :as async]))

(defmacro go-guard [m & body]
  `(async/go
    (try
      (do ~@body)
      (catch Throwable t#
        (doseq [c# ~m]
          (async/>! c# t#))
        t#))))

(defmacro <? [ch]
  `(let [result# (async/<! ~ch)]
     (when (instance? Throwable result#)
       (throw result#))
     result#))

(defmacro <?? [ch]
  `(let [result# (async/<!! ~ch)]
     (when (instance? Throwable result#)
       (throw result#))
     result#))
     
(defmacro <?+
  ([ch] (<?+ &form &env ch 1000 :timeout))
  ([ch time] (<?+ &form &env ch time :timeout))
  ([ch time timeout-val]
   `(async/alt! ~ch ([r#] (when (instance? Throwable r#)
                           (throw r#))
                          r#)
                (async/timeout ~time) ~timeout-val)))

(defmacro <??+
  ([ch] (<??+ &form &env ch 1000 :timeout))
  ([ch time] (<??+ &form &env ch time :timeout))
  ([ch time timeout-val]
   `(async/alt!! ~ch ([r#] (when (instance? Throwable r#)
                             (throw r#))
                           r#)
                 (async/timeout ~time) ~timeout-val)))
    
(intern 'clojure.core.async (with-meta 'go-guard {:macro true})
        @#'kotyo.async/go-guard)

(intern 'clojure.core.async (with-meta '<? {:macro true})
        @#'kotyo.async/<?)
(intern 'clojure.core.async (with-meta '<?? {:macro true})
        @#'kotyo.async/<??)

(intern 'clojure.core.async (with-meta '<?+ {:macro true})
        @#'kotyo.async/<?+)
(intern 'clojure.core.async (with-meta '<??+ {:macro true})
        @#'kotyo.async/<??+)

