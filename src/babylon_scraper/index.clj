(ns babylon_scraper.index
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [babylon_scraper.scrape :as scrape]
            [clucie.core :as core]
            [clucie.analysis :as analysis]
            [clucie.store :as store]))

(def analyser (analysis/standard-analyzer))

(def index-store (store/memory-store))

(defn scrape-content []
  (into [] (scrape/generate-condition-urls)))

(defn index []
  (core/add! index-store
             (into [] (scrape-content))
             [:title :url :content]
             analyser))

(defn search [search-string]
  (core/search index-store
             {:title search-string}
             10 ; max-num
             analyser
             0
             5))
