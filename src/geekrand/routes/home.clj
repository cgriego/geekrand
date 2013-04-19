(ns geekrand.routes.home
  (:use compojure.core hiccup.element hiccup.form geekrand.game)
  (:require [geekrand.views.layout :as layout]
            [geekrand.util :as util]))

(defn home-page [username-param]
  (let [username (if-not (nil? username-param) username-param "DGM Library")
        game (random-game username)]
    (layout/common
      (form-to {:style "margin: 10px 0;"} [:get "/"]
        [:div {:class "input-append"}
          (text-field "username" username)
          [:button {:type "submit" :class "btn"} "Randomize"]])
      (if (nil? game)
        [:p {:class "lead"} [:strong "You don't have any games!"]]
        (link-to (game-url game) [:h1 (:name game)] (image (:thumbnail game) ""))))))

(defroutes home-routes
  (GET "/" [username] (home-page username)))
