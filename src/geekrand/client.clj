(ns geekrand.client
  (:require [clj-http.client :as http]))

(defn thing-xml [id]
  (:body (http/get "http://www.boardgamegeek.com/xmlapi2/thing"
           {:query-params
            {:id id
             :stats 1}})))

(defn collection-xml [username include-expansions]
  (:body (http/get "http://www.boardgamegeek.com/xmlapi2/collection"
            {:query-params
             {:own 1
              :subtype "boardgame"
              :excludesubtype (if include-expansions nil "boardgameexpansion")
              :username username}})))
