(ns geekrand.routes.home
  (:use compojure.core hiccup.element hiccup.form geekrand.game)
  (:require [geekrand.views.layout :as layout]
            [geekrand.util :as util]))

(defn abs [number]
  (if (< number 0) (* number -1) number))

(defn ordinal-suffix [rank]
  (let [abs-rank (abs rank)]
    (if (contains? (set [11 12 13]) (mod abs-rank 100))
      "th"
      (case (mod abs-rank 10)
        1 "st"
        2 "nd"
        3 "rd"
        "th"))))

(defn game-rank [bgg-rank]
  (if-not (= "Not Ranked" bgg-rank)
    (let [bgg-rank-int (Integer/parseInt bgg-rank)]
      (list (format "%,d" bgg-rank-int) (ordinal-suffix bgg-rank-int) " on BGG" [:br]))))

(defn game-expands [expands combined-collections]
  (let [collected-ids (map :objectid combined-collections)
        expands-in-collections (filter (fn [expanded-game] (some (fn [id] (= (:objectid expanded-game) id)) collected-ids)) expands)]
    (if-not (empty? expands-in-collections)
      (list
        "Expands: "
        (interpose
          ", "
          (map
            (fn [game]
              (link-to (game-url game) (:name game)))
            expands-in-collections))))))

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
          [:li (link-to "/?username=cgriego" "cgriego")]
          [:li (link-to "/?username=Aldie%2C+derk" "Aldie, derk")]])
      (let [
        usernames (distinct (clojure.string/split username #"\s*,\s*"))
        combined-collections (multi-user-games usernames)
        game (random-game-from-games combined-collections)
       ]
        (if (nil? game)
          [:p.lead [:strong "You don't have any games!"]]
          (let [game (game-details (:objectid game))]
            (list
              (link-to (game-url game) [:h2 (:name game)] [:p (image (:thumbnail game) "")])
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
                (game-rank (:bgg-rank game))
                (game-expands (:expands game) combined-collections)])))))))

(defroutes home-routes
  (GET "/" [username] (home-page username)))
