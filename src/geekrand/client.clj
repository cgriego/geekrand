(ns geekrand.client
  (:require [clj-http.client :as http]))

(defn collection-xml [username]
  (:body (http/get "http://www.boardgamegeek.com/xmlapi2/collection"
            {:query-params
             {:own 1
              :subtype "boardgame"
              :username username}})))
