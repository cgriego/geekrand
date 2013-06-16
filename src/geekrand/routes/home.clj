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
        collected-game (random-game-from-games combined-collections)
        ;collected-game (first (filter #(= (:objectid %) "6411") combined-collections))
       ]
        (if (nil? collected-game)
          [:p.lead [:strong "You don't have any games!"]]
          (let [game-details (get-game-details (:objectid collected-game))]
            (list
              (link-to (game-url collected-game) [:h2 (:name collected-game)] [:p (image (:thumbnail collected-game) "")])
              [:p
                (if (= (:min-players game-details) (:max-players game-details))
                  (if-not (= 0 (:min-players game-details))
                    (list (:min-players game-details) " Players"))
                  (list (:min-players game-details) "-" (:max-players game-details) " Players"))
                [:br]
                (if-not (= 0 (:playing-time game-details))
                  (list
                    (:playing-time game-details) " Minute" (if-not (= 1 (:playing-time game-details)) "s") [:br]))
                (if-not (= 0 (:min-age game-details))
                  (list (:min-age game-details) " and Older" [:br]))
                (game-rank (:bgg-rank game-details))
                (game-expands (:expands game-details) combined-collections)])))))))

(defroutes home-routes
  (GET "/" [username] (home-page username)))
