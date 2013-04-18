(ns geekrand.routes.home
  (:use compojure.core hiccup.element geekrand.game)
  (:require [geekrand.views.layout :as layout]
            [geekrand.util :as util]))

(defn home-page []
  (let [game (random-game "DGM Library")]
    (layout/common
      (link-to (game-url game) [:h1 (:name game)] (image (:thumbnail game) "")))))

(defroutes home-routes
  (GET "/" [] (home-page)))
