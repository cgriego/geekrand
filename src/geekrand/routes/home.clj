(ns geekrand.routes.home
  (:use compojure.core hiccup.element geekrand.game)
  (:require [geekrand.views.layout :as layout]
            [geekrand.util :as util]
            [geekrand.client :as client]
            [clojure.xml]
            [clojure.zip]
            [clojure.data.zip.xml]))

(defn get-games [username]
  (let [xml (-> (client/get-collection-stream username) clojure.xml/parse clojure.zip/xml-zip)
        items (clojure.data.zip.xml/xml-> xml :item)]
    (map (fn [item]
      (map->Game {
        :objectid (clojure.data.zip.xml/xml1-> item (clojure.data.zip.xml/attr :objectid))
        :name     (clojure.data.zip.xml/xml1-> item :name clojure.data.zip.xml/text)
        :image    (clojure.data.zip.xml/xml1-> item :image clojure.data.zip.xml/text)
      })
    ) items)))

(defn get-random-game [username]
  (rand-nth (get-games username)))

(defn home-page []
  (let [game (get-random-game "DGM Library")]
    (layout/common
      [:h1 (link-to (game-url game) (:name game) [:br] (image (:image game) ""))])))

(defroutes home-routes
  (GET "/" [] (home-page)))
