(ns geekrand.game
  (:require [geekrand.client :as client]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]))

(defrecord Game [name objectid image thumbnail])

(defn game-url [{:keys [objectid]}]
  (str "http://boardgamegeek.com/boardgame/" objectid))

(defn xml-zip->Game [item]
  (map->Game { :objectid  (zip-xml/xml1-> item (zip-xml/attr :objectid))
               :name      (zip-xml/xml1-> item :name zip-xml/text)
               :image     (zip-xml/xml1-> item :image zip-xml/text)
               :thumbnail (zip-xml/xml1-> item :thumbnail zip-xml/text)}))

(defn xml-stream->games [xml-stream]
  (map xml-zip->Game
       (-> xml-stream
           xml/parse
           zip/xml-zip
           (zip-xml/xml-> :item))))

(defn games [username]
  (xml-stream->games (client/collection-stream username)))

(defn random-games [amount username]
  (take amount (shuffle (games username))))

(defn random-game [username]
  (rand-nth (games username)))
