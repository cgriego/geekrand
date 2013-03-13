(ns geekrand.game)

(defrecord Game [name objectid image])

(defn game-url [game]
  (str "http://boardgamegeek.com/boardgame/" (:objectid game)))
