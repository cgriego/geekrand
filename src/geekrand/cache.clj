(ns geekrand.cache
  (:refer-clojure :exclude [set get])
  (:require [clojurewerkz.spyglass.client :as memcache]))

(defonce connection (atom nil))

(defn connect! []
  (reset! connection (memcache/bin-connection "localhost:11211")))

(defn disconnect! []
  (memcache/shutdown @connection)
  (reset! connection nil))

(defn get [key]
  (memcache/get @connection key))

(defn set
  ([key value]
    (memcache/set @connection key 0 value))
  ([key value expiration]
    (memcache/set @connection key expiration value)))

(defn fetch [key expiration value-function]
  (let [cache-value (memcache/get @connection key)]
    (if (nil? cache-value)
      (let [value (value-function)]
        (set key value expiration)
        value)
      cache-value)))
