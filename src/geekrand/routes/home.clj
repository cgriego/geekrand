(ns geekrand.routes.home
  (:use compojure.core hiccup.element geekrand.game)
  (:require [geekrand.views.layout :as layout]
            [geekrand.util :as util]))

(defn home-page []
  (let [game (random-game "DGM Library")]
    (layout/common
      [:h1 (link-to (game-url game) (:name game) [:br] (image (:thumbnail game) ""))])))

(defroutes home-routes
  (GET "/" [] (home-page)))
