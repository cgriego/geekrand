(ns geekrand.game
  (:require [geekrand.client :as client]
            [geekrand.cache :as cache]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml])
  (:import [java.io ByteArrayInputStream]))

(defrecord Game [name objectid image thumbnail])
(defrecord GameDetails [name objectid image thumbnail min-players max-players playing-time min-age bgg-rank expands])

(defn game-url [{:keys [objectid]}]
  (str "http://boardgamegeek.com/boardgame/" objectid))

(defn string->stream [^String string]
  (java.io.ByteArrayInputStream. (.getBytes string "UTF-8")))

(defn xml-zip->Game [item]
  (map->Game { :objectid  (zip-xml/xml1-> item (zip-xml/attr :objectid))
               :name      (zip-xml/xml1-> item :name      zip-xml/text)
               :image     (zip-xml/xml1-> item :image     zip-xml/text)
               :thumbnail (zip-xml/xml1-> item :thumbnail zip-xml/text)}))

(defn xml->xml-zip [xml]
  (-> xml string->stream xml/parse zip/xml-zip))

(defn xml->games [xml]
  (map xml-zip->Game (zip-xml/xml-> (xml->xml-zip xml) :item)))

; 0 games = 5 minutes
; 30 games = 5 minutes
; 31 games = 6 minutes
; 60 games = 35 minutes
; 100 games = 1 hour, 15 minutes
; 500 games = 7 hours, 55 minutes
; 1,000 games = 16 hours, 15 minutes
; 1,425 games = 1 day
; 1,426 games = 1 day
; 2,000 games = 1 day
(defn collection-expiration [collection-xml]
  (let [adjusted-collection-size (- 25 (count (xml->games collection-xml)))]
    (* 60 (cond
      (> adjusted-collection-size (* 60 24)) (* 60 24) ; maximum 1 day
      (< adjusted-collection-size) 5 ; minimum 5 minutes
      :else adjusted-collection-size))))

(defn games [username include-expansions]
  (xml->games
    (cache/fetch
      (str "geekrand:collection:" username ":" include-expansions)
      collection-expiration
      (fn [] (client/collection-xml username include-expansions)))))

(defn multi-user-games [usernames include-expansions]
  (distinct (flatten (pmap #(games % include-expansions) usernames))))

(defn random-games [amount usernames]
  (take amount (shuffle (multi-user-games usernames))))

(defn random-game-from-games [games]
  (if (empty? games) nil (rand-nth games)))

(defn random-game [usernames]
  (let [games (multi-user-games usernames)]
    (random-game-from-games games)))

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
                        :bgg-rank     (zip-xml/xml1-> item :statistics :ratings :ranks :rank (zip-xml/attr :value))
                        :expands      (map
                                        (fn [link]
                                          (map->Game {
                                            :objectid (zip-xml/xml1-> link (zip-xml/attr :id))
                                            :name     (zip-xml/xml1-> link (zip-xml/attr :value))}))
                                        (zip-xml/xml-> item :link (zip-xml/attr= :type "boardgameexpansion") (zip-xml/attr= :inbound "true")))})))

(defn get-game-details [objectid]
  (xml->game-details (client/thing-xml objectid)))
