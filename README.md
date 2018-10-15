# kotyo.async

A Clojure library designed to extend the well known `clojure.core.async` library with some extra macros.

## Usage

Add the following to your `project.clj`'s dependencies field.
```clj
[kotyo/async "0.1.0"]
```

1. You have to require `kotyo.async` in a central file in your project. It will extend the `clojure.core.async` package with the extra macros.
2. Now you can use `clojure.core.async` as before and extra macros are available in the same namespace as the async ones.

### Extra macros
#### go-guard
This macro extends the `go` macro.
The first parameter is a vector of channels where it will put the first exception thrown in the body. If there is no exception the channels in the vector remain untouched.

It also puts the exception to the returned channel. 

```clj
(require '[clojure.core.async :as a]
         '[kotyo.async])

(def err-ch (a/chan))

(a/go-guard [err-ch]
  (throw (Exception. "Oh-no!")))
; It will put the exception to the err-ch and to the returned channel as well.

(a/<!! err-ch)
; #error { :cause "Oh-no!" ...
```
#### <?, <??
These reader macros are the same as the `<!`, `<!!` ones. Except these throw exceptions arrived on the channel.

```clj
(a/go-guard [err-ch]
  (throw (Exception. "Oh-no!")))

(try 
  (a/<?? err-ch) 
  (catch Throwable t 
    (println "Exception thrown:" t)))
; Exception thrown: ...
```

#### <?+, <??+
These are the same macros as the previous ones but with 2 extra optional parameters: `timeout-ms` and `timeout-value`.

```clj
(a/<??+ err-ch 500 :no-errors-so-far)
; ~ After 500 ms ~:
; :no-errors-so-far
```

## License

done by [kotyo](http://kotyo.net) in 2018

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
