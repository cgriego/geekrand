(ns geekrand.game
  (:require [geekrand.client :as client]
            [geekrand.cache :as cache]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml])
  (:import [java.io ByteArrayInputStream]))

(defrecord Game [name objectid image thumbnail])
(defrecord GameDetails [name objectid image thumbnail min-players max-players playing-time min-age bgg-rank])

(defn game-url [{:keys [objectid]}]
  (str "http://boardgamegeek.com/boardgame/" objectid))

(defn string->stream [string]
  (java.io.ByteArrayInputStream. (.getBytes ^String string "UTF-8")))

(defn xml-zip->Game [item]
  (map->Game { :objectid  (zip-xml/xml1-> item (zip-xml/attr :objectid))
               :name      (zip-xml/xml1-> item :name      zip-xml/text)
               :image     (zip-xml/xml1-> item :image     zip-xml/text)
               :thumbnail (zip-xml/xml1-> item :thumbnail zip-xml/text)}))

(defn xml->xml-zip [xml]
  (-> xml string->stream xml/parse zip/xml-zip))

(defn xml->games [xml]
  (map xml-zip->Game (zip-xml/xml-> (xml->xml-zip xml) :item)))

(defn games [username]
  (xml->games
    (cache/fetch
      (str "geekrand:collection:" username)
      (* 60 5) ; 5 minutes
      (fn [] (client/collection-xml username)))))

(defn multi-user-games [usernames]
  (distinct (flatten (map games usernames))))

(defn random-games [amount usernames]
  (take amount (shuffle (multi-user-games usernames))))

(defn random-game [usernames]
  (let [games (multi-user-games usernames)]
    (if (empty? games)
      nil
      (rand-nth games))))

(defn xml->game-details [xml]
  (let [item (zip-xml/xml1-> (xml->xml-zip xml) :item)]
    (map->GameDetails { :objectid     (zip-xml/xml1-> item (zip-xml/attr :id))
                        :name         (zip-xml/xml1-> item :name        (zip-xml/attr :value))
                        :image        (zip-xml/xml1-> item :image       zip-xml/text)
                        :thumbnail    (zip-xml/xml1-> item :thumbnail   zip-xml/text)
                        :min-players  (Integer/parseInt (zip-xml/xml1-> item :minplayers     (zip-xml/attr :value)))
                        :max-players  (Integer/parseInt (zip-xml/xml1-> item :maxplayers     (zip-xml/attr :value)))
                        :playing-time (Integer/parseInt (zip-xml/xml1-> item :playingtime    (zip-xml/attr :value)))
                        :min-age      (Integer/parseInt (zip-xml/xml1-> item :minage         (zip-xml/attr :value)))
                        :bgg-rank     (zip-xml/xml1-> item :statistics :ratings :ranks :rank (zip-xml/attr :value))})))

(defn game-details [objectid]
  (xml->game-details (client/thing-xml objectid)))
