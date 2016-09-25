(ns babylon_scraper.index
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [babylon_scraper.scrape :as scrape]
            [clucie.core :as clcore]
            [clucie.analysis :as analysis]
            [clucie.store :as store]))

(def analyser (analysis/standard-analyzer))

(comment (def index-store (store/memory-store)))
(def index-store (store/disk-store "/Users/prasad/Downloads/babylon_index1"))

(defn scrape-content []
  (into [] (scrape/scrape-content)))

(defn combine-a-list [x a-vector]
  (clojure.string/join " " a-vector)
  )
(defn index-doc [content]
  (clcore/add! index-store
             [content] [:title :url :content]
             analyser))

(defn index-condition-page [condition-page]
  (index-doc (json/read-str condition-page :key-fn keyword))
  )

(defn read-json [scrapedjson]
  (with-open [rdr (io/reader scrapedjson)]
    (doseq [condition-page (line-seq rdr)]
      (comment (print condition-page))
      (index-condition-page condition-page)
      )))

(defn search [search-string]
  (core/search index-store
             {:title search-string}
             10 ; max-num
             analyser
             0
             5))
