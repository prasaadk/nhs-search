(ns babylon_scraper.scrape
  (:require [net.cgrand.enlive-html :as html]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def domain-url "http://www.nhs.uk/")
(def base-url "http://www.nhs.uk/Conditions/Pages/BodyMap.aspx?Index=")

(defn- fetch-url [url]
  (html/html-resource (java.net.URL. url)))

;; Generates a character range e.g for A - Z, use (char-range \A \Z))
(defn- char-range [start end]
  (map #(str (char %)) (range (int start) (inc (int end)))))

;; This method generates URLs for all the conditions
(defn- generate-index-urls []
  (map #(str base-url %) (concat (char-range \A \Z) (char-range \0 \9))))

(defn- page-url [url]
  (if (str/starts-with? url "/")
    (str domain-url url)
    url))

;; This method is supposed to fetch content for given condition page URL
(defn- page-content [url]
  (apply str (html/select (fetch-url (page-url url)) [:div.main-content html/text-node])))

;; This method fetch html content and return json-like objects
(defn- fetch-json-content [url]
  (map (fn [x] {:title (first (:content x))
               :url (:href (:attrs x))
               :content (page-content (:href (:attrs x)))} )
       (html/select (fetch-url url) [:div.index-section :> :ul :li :a])))

(defn generate-condition-urls []
  (flatten
   (map (fn [x] (fetch-json-content x))
        (generate-index-urls))))

;; this method writes the content to a file
(defn scrape [filename]
  (with-open [wrtr (io/writer filename)]
    (count (map (fn [x] (.write wrtr (str (json/write-str x) "\n")))
                (generate-condition-urls)))))

;; scratchpad / work area ahead

(comment (char-range \0 \9))

(comment (map (fn [x] [(first (:content x)) (:href (:attrs x))]) (html/select (fetch-url "http://www.nhs.uk/Conditions/Pages/BodyMap.aspx?Index=A") [:div.index-section :> :ul :li :a])))
