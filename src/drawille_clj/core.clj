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
