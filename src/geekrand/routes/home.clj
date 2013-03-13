(ns geekrand.routes.home
  (:use compojure.core hiccup.element)
  (:require [geekrand.views.layout :as layout]
            [geekrand.util :as util]
            [geekrand.client :as client]
            [clojure.xml]))

(defn get-random-game []
  (rand-nth (:content
    (clojure.xml/parse (:body (client/get-collection-stream "DGM Library"))))))

(defn home-page []
  (let [game (get-random-game)
        game-name (first (:content (first (:content game))))
        game-id (:objectid (:attrs game))
        game-image (first (:content (nth (:content game) 2)))
        game-url (str "http://boardgamegeek.com/boardgame/" game-id)]
    (layout/common
      [:h1 (link-to game-url game-name [:br] (image game-image ""))])))

(defroutes home-routes
  (GET "/" [] (home-page)))
