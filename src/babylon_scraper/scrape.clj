(ns babylon_scraper.scrape
  (:require [net.cgrand.enlive-html :as html]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def domain-url "http://www.nhs.uk/")
(def base-url "http://www.nhs.uk/Conditions/Pages/BodyMap.aspx?Index=")

;; This method fetches HTML for the given [url]
(defn- get-html-content [url]
  (html/html-resource (java.net.URI. (clojure.string/replace (clojure.string/trim url) #"\s" "%20"))))

;; This method extracts condition page links for the given index page url
(defn- extract-condition-page-links [index-page-url]
  (html/select (get-html-content index-page-url) [:div.index-section :> :ul :li :a]))

;; Generates a character range e.g for A - Z, use (char-range \A \Z))
(defn- char-range [start end]
  (map #(str (char %)) (range (int start) (inc (int end)))))

;; This method generates URLs covering all the Index characters
;; http://www.nhs.uk/Conditions/Pages/BodyMap.aspx?Index=[A-Z|0-9] 
(defn- generate-seed-urls []
  (map #(str base-url %) (concat (char-range \A \Z) (char-range \0 \9))))

;; This checks if the retrieved URL is relative or not.
(defn- page-url [url]
  (if (str/starts-with? url "/")
    (str domain-url url)
    url))

;; This method fetches content for given condition page URL
(defn- page-content [url]
  (get-html-content (page-url url)))

;; Extract main-content
(defn- extract-main-content [content]
  (html/select content [:div.main-content html/text-node]))

(defn- extract-title [content]
  (first (html/select content [:div.healthaz-header :h1 html/text-node])))

;; Find sub-page-urls
(defn- find-sub-page-urls [content]
  (html/select content [:ul.sub-nav :li :span :a]))

;; Extract and scrape sub-page-urls main-content
(defn- extract-sub-page-content [content]
  (map (fn [x]
         (let [sub-content (page-content (:href (:attrs x)))]
           {:title (extract-title sub-content)
            :url (page-url (:href (:attrs x)))
            :content (extract-main-content sub-content)}))
       (find-sub-page-urls content)))

;; This method attempts to scrape the sub pages
(defn- scrape-condition-pages [url]
  (map (fn [x]
         (let [content (page-content (:href (:attrs x)))]
           (conj (flatten (extract-sub-page-content content)) ;; extract sub-pages
                 {:title (first (:content x))
                  :url (page-url (:href (:attrs x)))
                  :content (extract-main-content content)} ;; element for main-page
                 )
           ))
       (extract-condition-page-links url)))

;; 
(defn scrape-content []
  (flatten
   (map (fn [x] (scrape-condition-pages x))
        (generate-seed-urls))))

;; this method writes the content to a file
(defn scrape [filename]
  (with-open [wrtr (io/writer filename)]
    (count (map (fn [x] (.write wrtr (str (json/write-str x) "\n")))
                (scrape-content)))))
