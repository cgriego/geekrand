(ns geekrand.client
  (:require [clj-http.client :as http]))

(defn get-collection
  [username]
  (http/get "http://www.boardgamegeek.com/xmlapi2/collection"
            {:query-params {:own 1
                            :subtype "boardgame"
                            :username username}}))
