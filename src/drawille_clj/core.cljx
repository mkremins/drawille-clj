(ns drawille-clj.core
  (:refer-clojure :exclude [set])
  (:require [clojure.string :as string]))

(defn ->canvas
  "Returns a blank canvas with `w` (multiple of 2) columns and `h` (multiple of
   4) rows."
  [w h]
  (assert (and (pos? w) (zero? (mod w 2))
               (pos? h) (zero? (mod h 4))))
  (vec (repeat (/ h 4) (vec (repeat (/ w 2) 0)))))

(defn clear
  "Returns a blank canvas with the same dimensions as `canvas`."
  [canvas]
  (->canvas (* 2 (count (first canvas))) (* 4 (count canvas))))

(defn- pt->cpath [[x y]]
  [(int (/ y 4)) (int (/ x 2))])

(def ^:private masks
  [[0x01 0x08]
   [0x02 0x10]
   [0x04 0x20]
   [0x40 0x80]])

(defn- pt->mask [[x y]]
  (get-in masks [(mod y 4) (mod x 2)]))

(defn set [canvas pt]
  (update-in canvas (pt->cpath pt) bit-or (pt->mask pt)))

(defn unset [canvas pt]
  (update-in canvas (pt->cpath pt) bit-and (bit-not (pt->mask pt))))

(defn toggle [canvas pt]
  (update-in canvas (pt->cpath pt) bit-xor (pt->mask pt)))

(defn- minmax [n1 n2]
  [(min n1 n2) (max n1 n2)])

(defn line
  "Returns a canvas like `canvas`, but with a straight line drawn between the
   two specified points."
  [canvas [x1 y1] [x2 y2]]
  (let [[min-x max-x] (minmax x1 x2)
        [min-y max-y] (minmax y1 y2)
        stepc (inc (- max-x min-x))
        step  (/ (inc (- max-y min-y)) stepc)
        steps (->> min-y (iterate (partial + step)) (take stepc) (map int))
        xs (range min-x (inc max-x))
        ys (if (or (and (= min-x x1) (not= min-y y1))
                   (and (not= min-x x1) (= min-y y1)))
               (reverse steps) steps)]
    (reduce set canvas (zipmap xs ys))))

(def ^:private braille-char-offset 0x2800)

(defn- cc->ch [cc]
  (char (+ braille-char-offset cc)))

(defn canvas->str
  "Renders `canvas` to a newline-delimited string. The string will contain
   `h/4` lines, each having `w/2` characters, where `w` and `h` are the width
   and height of `canvas` respectively."
  [canvas]
  (->> canvas
       (map #(->> % (map cc->ch) string/join))
       (string/join "\n")))
