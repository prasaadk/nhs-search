(ns babylon_scraper.scrape
  (:require [net.cgrand.enlive-html :as html]
            [clojure.data.json :as json]
            [clojure.java.io :as io]))

(def base-url "http://www.nhs.uk/Conditions/Pages/BodyMap.aspx?Index=")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

;; Generates a character range e.g for A - Z, use (char-range \A \Z))
(defn char-range [start end]
  (map #(str (char %)) (range (int start) (inc (int end)))))

;; This method generates URLs for all the conditions
(defn generate-index-urls []
  (map #(clojure.string/join "" [base-url %]) (concat (char-range \A \Z) (char-range \0 \9))))

;; This method is supposed to fetch content for given condition page URL
(defn page-content [url]
  "TODO: fetch content"
  )

;; This method fetch html content and return json-like objects
(defn fetch-json-content [url]
  (map (fn [x] {:page-title (first (:content x))
               :page-url (:href (:attrs x))
               :page-content (page-content (:href (:attrs x)))} )
       (html/select (fetch-url url) [:div.index-section :> :ul :li :a])))

(defn generate-condition-urls []
  (map (fn [x] (fetch-json-content x)) (generate-index-urls)))

;; this method writes the content to a file
(defn scrape [filename]
  (with-open [wrtr (io/writer filename)]
    (doall (map (fn [x] (.write wrtr (str (json/write-str x) "\n")))
                (flatten (generate-condition-urls))))))

;; scratchpad / work area ahead

(comment (char-range \0 \9))

(comment (map (fn [x] [(first (:content x)) (:href (:attrs x))]) (html/select (fetch-url "http://www.nhs.uk/Conditions/Pages/BodyMap.aspx?Index=A") [:div.index-section :> :ul :li :a])))
