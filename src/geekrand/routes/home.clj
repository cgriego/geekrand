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
          (let [game (game-details (:objectid game))]
            (list
              (link-to (game-url game) [:h1 (:name game)] [:p (image (:thumbnail game) "")])
              [:p
                (if (= (:min-players game) (:max-players game))
                  (if-not (= 0 (:min-players game))
                    (list (:min-players game) " Players"))
                  (list (:min-players game) "-" (:max-players game) " Players"))
                [:br]
                (if-not (= 0 (:playing-time game))
                  (list
                    (:playing-time game) " Minute" (if-not (= 1 (:playing-time game)) "s") [:br]))
                (if-not (= 0 (:min-age game))
                  (list (:min-age game) " and Older" [:br]))
                (if-not (= "Not Ranked" (:bgg-rank game))
                  (list (format "%,d" (Integer/parseInt (:bgg-rank game))) " on BGG"))])))))))

(defroutes home-routes
  (GET "/" [username] (home-page username)))
