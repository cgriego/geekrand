(ns geekrand.game
  (:require [geekrand.client :as client]
            [clojure.xml]
            [clojure.zip]
            [clojure.data.zip.xml]))

(defrecord Game [name objectid image thumbnail])

(defn game-url [game]
  (str "http://boardgamegeek.com/boardgame/" (:objectid game)))

(defn get-games [username]
  (let [xml (-> (client/get-collection-stream username) clojure.xml/parse clojure.zip/xml-zip)
        items (clojure.data.zip.xml/xml-> xml :item)]
    (map (fn [item]
      (map->Game {
        :objectid  (clojure.data.zip.xml/xml1-> item (clojure.data.zip.xml/attr :objectid))
        :name      (clojure.data.zip.xml/xml1-> item :name clojure.data.zip.xml/text)
        :image     (clojure.data.zip.xml/xml1-> item :image clojure.data.zip.xml/text)
        :thumbnail (clojure.data.zip.xml/xml1-> item :thumbnail clojure.data.zip.xml/text)
      })
    ) items)))

(defn get-random-games [amount username]
  (take amount (shuffle (get-games username))))

(defn get-random-game [username]
  (rand-nth (get-games username)))
