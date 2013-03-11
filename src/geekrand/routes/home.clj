(ns geekrand.routes.home
  (:use compojure.core hiccup.element)
  (:require [geekrand.views.layout :as layout]
            [geekrand.util :as util]
            [clojure.xml]))

(defn get-random-game []
  (rand-nth (:content
    (clojure.xml/parse "http://www.boardgamegeek.com/xmlapi2/collection?own=1&subtype=boardgame&username=DGM Library"))))

(defn home-page []
  (let [game (get-random-game)
        game-name (first (:content (first (:content game))))
        game-id (:objectid (:attrs game))
        game-url (str "http://boardgamegeek.com/boardgame/" game-id)]
    (layout/common
      [:h1 (link-to game-url game-name)])))

(defroutes home-routes
  (GET "/" [] (home-page)))
