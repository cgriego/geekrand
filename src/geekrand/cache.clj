(ns geekrand.cache
  (:refer-clojure :exclude [set get])
  (:require [clj-xmemcached.core :as memcache])
  (:import [net.rubyeye.xmemcached XMemcachedClient XMemcachedClientBuilder]
           [net.rubyeye.xmemcached.auth AuthInfo]
           [net.rubyeye.xmemcached.command BinaryCommandFactory]
           [net.rubyeye.xmemcached.impl ArrayMemcachedSessionLocator]
           [net.rubyeye.xmemcached.transcoders SerializingTranscoder]
           [net.rubyeye.xmemcached.utils AddrUtil]))

(defn memcached-auth [servers username password]
  (delay
    (let [builder (doto (XMemcachedClientBuilder. (AddrUtil/getAddresses servers))
                        (.addAuthInfo (AddrUtil/getOneAddress servers) (AuthInfo/plain username password))
                        (.setTranscoder (SerializingTranscoder.))
                        (.setSessionLocator (ArrayMemcachedSessionLocator.))
                        (.setConnectionPoolSize 1)
                        (.setCommandFactory (BinaryCommandFactory.)))
          rt (.build builder)]
      (doto rt
        (.setOpTimeout 5000)
        (.setEnableHealSession true)
        (.setEnableHeartBeat true)
        (.setSanitizeKeys false)))))

(defn connect! []
  (memcache/set-client!
    (if (System/getenv "MEMCACHIER_SERVERS")
      (memcached-auth
          (System/getenv "MEMCACHIER_SERVERS")
          (System/getenv "MEMCACHIER_USERNAME")
          (System/getenv "MEMCACHIER_PASSWORD"))
      (memcache/memcached "localhost:11211" :protocol :binary))))

(defn disconnect! []
  (memcache/shutdown))

(defn get [key]
  (memcache/get key))

(defn set
  ([key value]
    (memcache/set key value 0))
  ([key value expiration]
    (memcache/set key value expiration)))

(defn fetch [key expiration value-function]
  (let [cache-value (memcache/get key)]
    (if (nil? cache-value)
      (let [value (value-function)]
        (set key value expiration)
        value)
      cache-value)))
