(ns dev
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
    [com.stuartsierra.component :as component]
    [clojure.java.io :as io]
    [clojure.java.javadoc :refer [javadoc]]
    [clojure.pprint :refer [pprint]]
    [clojure.reflect :refer [reflect]]
    [clojure.repl :refer [apropos dir doc find-doc pst source]]
    [clojure.set :as set]
    [clojure.string :as str]
    [clojure.test :as test]
    [clojure.tools.namespace.repl :refer [refresh refresh-all]]
    [viaz.system :as system]))

(def system
  "A Var containing an object representing the application under
  development."
  nil)

(def zimbra-7-env
  {:http-server-port            3000
   :zimbra-base-url             "http://zimbra.ergon.ch/home/"
   :zimbra-calendar-partial-url "/viaz.xml"
   :zimbra-client-options       {}})

(def zimbra-8-env
  {:http-server-port            3000
   :zimbra-base-url             "https://vetter.ergon.ch/service/home/"
   :zimbra-calendar-partial-url "/Calendar/viaz.xml"
   :zimbra-client-options       {:insecure? true}})

(defn init
  "Creates and initializes the system under development in the Var
  #'system."
  []
  (alter-var-root #'system
                  (constantly (system/system
                                zimbra-7-env))))

(defn start
  "Starts the system running, updates the Var #'system."
  []
  (alter-var-root #'system component/start))

(defn stop
  "Stops the system if it is currently running, updates the Var
  #'system."
  []
  (alter-var-root #'system (fn [s]
                             (when s (component/stop s)))))

(defn go
  "Initializes and starts the system running."
  []
  (init)
  (start)
  :ready)

(defn reset
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (refresh :after `go))

(comment
  (reset))
