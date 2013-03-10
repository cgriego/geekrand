(ns geekrand.routes.home
  (:use compojure.core hiccup.element)
  (:require [geekrand.views.layout :as layout]
            [geekrand.util :as util]))

(defn home-page []
  (layout/common
    (util/md->html "/md/docs.md")))

(defn about-page []
  (layout/common
   "this is the story of geekrand... work in progress"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))
