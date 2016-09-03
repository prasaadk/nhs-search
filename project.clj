(defproject babylon-scraper "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [enlive "1.1.6"]
                 [org.clojure/data.json "0.2.6"]
                 [clucie "0.1.5"]]
  :main ^:skip-aot babylon-scraper.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
