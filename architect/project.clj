(defproject architect "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                [org.clojure/clojure "1.5.1"]
                [org.apache.commons/commons-math3 "3.1.1"]
                ]
  :main architect.core
  :profiles {:uberjar {:aot :all}})
