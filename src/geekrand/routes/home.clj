(ns geekrand.routes.home
  (:use compojure.core hiccup.element hiccup.form geekrand.game)
  (:require [geekrand.views.layout :as layout]
            [geekrand.util :as util]))

(defn home-page [username]
  (layout/common
    (form-to {:style "margin: 10px 0;"} [:get "/"]
      [:div.input-append
        (text-field {:placeholder "BGG Username"} "username" username)
        [:button.btn {:type "submit"} "Randomize"]])
    (if (empty? username)
      (list
        [:p.lead [:strong "Example Users"]]
        [:ul
          [:li (link-to "/?username=DGM+Library" "DGM Library")]
          [:li (link-to "/?username=TomVasel" "TomVasel")]
          [:li (link-to "/?username=cgriego" "cgriego")]])
      (let [game (random-game username)]
        (if (nil? game)
          [:p.lead [:strong "You don't have any games!"]]
          (link-to (game-url game) [:h1 (:name game)] (image (:thumbnail game) "")))))))

(defroutes home-routes
  (GET "/" [username] (home-page username)))
