# drawille-clj
drawille-clj is a Clojure library that lets you draw in the terminal using Unicode Braille characters. It's inspired by [Drawille](https://github.com/asciimoo/drawille), which does more or less the same thing but in Python.

## Usage
Add to your `project.clj`:

```clojure
[mkremins/drawille-clj "0.1.0"]
```

Pull in the namespace:

```clojure
(:require [drawille-clj.core :as d])
```

## API

#### (->canvas w h)
Returns a blank canvas with `w` (multiple of 2) columns and `h` (multiple of 4) rows. (A "canvas" is just a vector of vectors of characters, where each vector at the top level represents one "row" or "line". This is subject to change in the future.)

#### (clear canvas)
Returns a blank canvas with the same dimensions as `canvas`.

#### (set canvas [x y])
Returns a canvas like `canvas`, but with the point `[x y]` enabled.

#### (unset canvas [x y])
Returns a canvas like `canvas`, but with the point `[x y]` disabled.

#### (toggle canvas [x y])
Returns a canvas like `canvas`, but with the point `[x y]` toggled.

#### (canvas->str canvas)
Renders `canvas` to a newline-delimited string. The string will contain `h/4` lines, each having `w/2` characters, where `w` and `h` are the width and height of `canvas` respectively.

## License
[MIT License](http://opensource.org/licenses/MIT). Hack away.
