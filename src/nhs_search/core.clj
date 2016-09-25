(ns nhs_search.core
  (:gen-class)
  (:require [nhs_search.index :as index]
            [nhs_search.scrape :as scrape]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (scrape/scrape "/tmp/conditions.json")
  (index/index-file "/tmp/conditions.json"))
