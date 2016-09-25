(ns nhs_search.index
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [nhs_search.scrape :as scrape]
            [clucie.core :as clcore]
            [clucie.analysis :as analysis]
            [clucie.store :as store]))

(def analyser (analysis/standard-analyzer))

(comment (def index-store (store/memory-store)))
(def index-store (store/disk-store "/Users/prasad/Downloads/nhs_index"))

(defn scrape-content []
  (into [] (scrape/scrape-content)))

(defn index-doc [content]
  (clcore/add! index-store
             [content] [:title :url :content]
             analyser))

;; Index condition page object as a document with lucene/clucie
(defn index-condition-page [condition-page]
  (index-doc (json/read-str condition-page :key-fn keyword))
  )

;; This method indexes a conditions json file.
(defn index-file [scrapedjson]
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
