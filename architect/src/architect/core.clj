(ns architect.core
  	(:gen-class
  		:methods 
        [
        #^{:static true} [calfitness [float float float] float]
        #^{:static true} [mutator [int int int int] double]
        ]
      )
    (:require
      [architect.engine :as engine]))



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  )

(defn -calfitness 
  [gen n mu]
  (float (* (* (/ 1 gen) n) mu)))

;; Behave to the fitness function as heuristic
(defn fitness-funct [^doubles xs]
  ;; square of every member fitness value cast it and map it
  ;; it means our members will diverge 
    (let [^doubles squares (amap ^doubles xs idx ret
                                 (Math/pow (aget ^doubles xs idx) 2))]
      (areduce ^doubles squares idx ret (double 0)
               (+ ret (aget ^doubles squares idx)))))

;;
;; (defn runge-kutta
;;    [^doubles xs]
;;    [fps initvals depvar depv0 stepsz]
;;    (numeric/rk4 xs ))

 ;;
 ;; This uses examplanary population mutation factor
 ;; float popMu = (1/(float)GEN) * (float)N * MU;
 ;;

(defn -mutator
  ;; int int int int
  [size lowerbound upperbound maxiter]
  (println "Expected population size: " size "LowerBound: " lowerbound "UpperBound: " upperbound "Maxiter: " maxiter)
  (.mutation-factor (engine/optimized fitness-funct 0.2 0.1 size 10 lowerbound upperbound maxiter))
  )

;;(optimize fitness 0.2 0.1 100 10 -100 100 150)


;;(-mutator (java.util.ArrayList. [1 2 3 4 5 6 7 8 9 10 11 12]) -100 100 100)


;;(engine/optimized fitness-funct 0.2 0.1 100 10 -100 100 100)
;;(println (engine/optimized fitness-funct 0.2 0.1 100 10 -100 100 100))
 
