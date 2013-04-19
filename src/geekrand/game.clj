(ns geekrand.game
  (:require [geekrand.client :as client]
            [geekrand.cache :as cache]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml])
  (:import [java.io ByteArrayInputStream]))

(defrecord Game [name objectid image thumbnail])

(defn game-url [{:keys [objectid]}]
  (str "http://boardgamegeek.com/boardgame/" objectid))

(defn- string->stream [string]
  (java.io.ByteArrayInputStream. (.getBytes ^String string "UTF-8")))

(defn- xml-zip->Game [item]
  (map->Game { :objectid  (zip-xml/xml1-> item (zip-xml/attr :objectid))
               :name      (zip-xml/xml1-> item :name zip-xml/text)
               :image     (zip-xml/xml1-> item :image zip-xml/text)
               :thumbnail (zip-xml/xml1-> item :thumbnail zip-xml/text)}))

(defn- xml->games [xml]
  (map xml-zip->Game
       (-> (string->stream xml)
           xml/parse
           zip/xml-zip
           (zip-xml/xml-> :item))))

(defn games [username]
  (xml->games
    (cache/fetch
      (str "geekrand:collection:" username)
      (* 60 5) ; 5 minutes
      (fn [] (client/collection-xml username)))))

(defn random-games [amount username]
  (take amount (shuffle (games username))))

(defn random-game [username]
  (let [games (games username)]
    (if (empty? games)
      nil
      (rand-nth games))))
