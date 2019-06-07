# Kyrie

[![Build status][travis-badge]][travis-badge-url]
[![Download](https://api.bintray.com/packages/alexjlockwood/maven/kyrie/images/download.svg)](https://bintray.com/alexjlockwood/maven/kyrie/_latestVersion)

Kyrie is a superset of Android's `VectorDrawable` and `AnimatedVectorDrawable` classes: it can do everything they can do and more.

![Screen capture of tool](art/screencap.gif)

## Motivation

`VectorDrawable`s are great because they provide density independence—they can be scaled arbitrarily on any device without loss of quality. `AnimatedVectorDrawable`s make them even more awesome, allowing us to animate specific properties of a `VectorDrawable` in a variety of ways.

However, these two classes have three main limitations:

1. They can't be paused, resumed, or seeked.
2. They can't be dynamically created at runtime (they must be inflated from a drawable resource).
3. They only support a small subset of features that SVGs provide on the web.

Kyrie was created in order to address these problems.

## Getting started

To create an animation using Kyrie, you first need to build a [`KyrieDrawable`][kyriedrawable]. There are two ways to do this:

### Option #1: from an existing VD/AVD resource

With Kyrie, you can convert an existing `VectorDrawable` or `AnimatedVectorDrawable` resource into a `KyrieDrawable` with a single line:

```kotlin
val drawable = KyrieDrawable.create(context, R.drawable.my_vd_or_avd);
```

### Option #2: programatically using a [`KyrieDrawable.Builder`][kyriedrawable#builder]

You can also build `KyrieDrawable`s at runtime using the builder pattern. `KyrieDrawable`s are similar to SVGs and `VectorDrawable`s in that they are tree-like structures built of [`Node`][node]s. As you build the tree, you can optionally assign [`Animation`][animation]s to the properties of each `Node` to create an animatable `KyrieDrawable`.

Here is a snippet of code from the [sample app][sample-app-source-code] that builds a material design circular progress indicator:

```kotlin
val drawable =
    kyrieDrawable {
        viewport = size(48f, 48f)
        tint = Color.RED
        group {
            translateX(24f)
            translateY(24f)
            rotation(
                Animation.ofFloat(0f, 720f)
                    .duration(4444)
                    .repeatCount(Animation.INFINITE)
            )
            path {
                strokeColor(Color.WHITE)
                strokeWidth(4f)
                trimPathStart(
                    Animation.ofFloat(0f, 0.75f)
                        .duration(1333)
                        .repeatCount(Animation.INFINITE)
                        .interpolator("M 0 0 h .5 C .7 0 .6 1 1 1".asPathInterpolator())
                )
                trimPathEnd(
                    Animation.ofFloat(0.03f, 0.78f)
                        .duration(1333)
                        .repeatCount(Animation.INFINITE)
                        .interpolator("M 0 0 c .2 0 .1 1 .5 1 C 1 1 1 1 1 1".asPathInterpolator())
                )
                trimPathOffset(
                    Animation.ofFloat(0f, 0.25f)
                        .duration(1333)
                        .repeatCount(Animation.INFINITE)
                )
                strokeLineCap(StrokeLineCap.SQUARE)
                pathData("M 0 -18 a 18 18 0 1 1 0 36 18 18 0 1 1 0 -36")
            }
        }
    }
```

## Features

Kyrie supports 100% of the features that `VectorDrawable`s and `AnimatedVectorDrawable`s provide. It also extends the functionality of `VectorDrawable`s and `AnimatedVectorDrawable`s in a number of ways, making it possible to create even more powerful and elaborate scalable assets and animations.

### `VectorDrawable` features

In addition to the features supported by `VectorDrawable`, Kyrie provides the following:

#### `<path>` features

- `CircleNode`. Equivalent to the `<circle>` node in SVG.
- `EllipseNode`. Equivalent to the `<ellipse>` node in SVG.
- `LineNode`. Equivalent to the `<line>` node in SVG.
- `RectangleNode`. Equivalent to the `<rect>` node in SVG.
- `strokeDashArray` (`FloatArray`). Equivalent to the `stroke-dasharray` attribute in SVG.
- `strokeDashOffset` (`Float`). Equivalent to the `stroke-dashoffset` attribute in SVG.
- `isScalingStroke` (`Boolean`). Equivalent to `vector-effect="non-scaling-stroke"` in SVG. Defines whether a path's stroke width will be affected by scaling transformations.
- The `strokeMiterLimit` attribute is animatable.

#### `<clip-path>` features

- `FillType` (either `NON_ZERO` or `EVEN_ODD`). Equivalent to the `clip-rule` attribute in SVG.
- `ClipType` (either `INTERSECT` or `DIFFERENCE`). Defines whether the clipping region is additive or subtractive.

#### `<group>` features

- Transformations (`pivot`, `scale`, `rotation`, and `translation`) can be set on _any_ `Node`, not just `GroupNode`s.

### `AnimatedVectorDrawable` features

In addition to the features supported by `AnimatedVectorDrawable`, Kyrie provides the following:

- [`setCurrentPlayTime(long)`][kyriedrawable#setcurrentplaytime].
  - Allows you to manually scrub the animation.
- [`pause()`][kyriedrawable#pause] and [`resume()`][kyriedrawable#resume].
  - Allows you to pause and resume the animation.
- [`addListener(KyrieDrawable.Listener)`][kyriedrawable#addlistener].
  - Allows you to listen for the following animation events: start, update, pause, resume, cancel, and end.

## Further reading

- Check out [this blog post][adp-blog-post] for more on the motivation behind the library.
- Check out [the sample app][sample-app-source-code] for example usages in both Java and Kotlin.
- Check out [the documentation][documentation] for a complete listing of all supported `Animation`s and `Node`s that can be used when constructing `KyrieDrawable`s programatically.

## Dependency

Add this to your root `build.gradle` file (_not_ your module's `build.gradle` file):

```gradle
allprojects {
    repositories {
        // ...
        jcenter()
    }
}
```

Then add the library to your module's `build.gradle` file:

```gradle
dependencies {
    // ...
    implementation 'com.github.alexjlockwood:kyrie:0.2.1'
}
```

## Compatibility

- **Minimum Android SDK**: Kyrie requires a minimum API level of 14.
- **Compile Android SDK**: Kyrie requires you to compile against API 28 or later.

[travis-badge]: https://travis-ci.org/alexjlockwood/kyrie.svg?branch=master
[travis-badge-url]: https://travis-ci.org/alexjlockwood/kyrie
[kyriedrawable]:https://alexjlockwood.github.io/kyrie/com.github.alexjlockwood.kyrie/-kyrie-drawable/index.html
[node]: https://alexjlockwood.github.io/kyrie/com.github.alexjlockwood.kyrie/-node/index.html
[animation]: https://alexjlockwood.github.io/kyrie/com.github.alexjlockwood.kyrie/-animation/index.html
[progressfragment]: https://github.com/alexjlockwood/kyrie/blob/master/sample/src/main/java/com/example/kyrie/ProgressFragment.kt
[kyriedrawable#setcurrentplaytime]: https://alexjlockwood.github.io/kyrie/com.github.alexjlockwood.kyrie/-kyrie-drawable/current-play-time.html
[kyriedrawable#pause]: https://alexjlockwood.github.io/kyrie/com.github.alexjlockwood.kyrie/-kyrie-drawable/pause.html
[kyriedrawable#resume]: https://alexjlockwood.github.io/kyrie/com.github.alexjlockwood.kyrie/-kyrie-drawable/resume.html
[kyriedrawable#addlistener]: https://alexjlockwood.github.io/kyrie/com.github.alexjlockwood.kyrie/-kyrie-drawable/add-listener.html
[kyriedrawable#builder]: https://alexjlockwood.github.io/kyrie/com.github.alexjlockwood.kyrie/-kyrie-drawable/-builder/index.html
[documentation]: https://alexjlockwood.github.io/kyrie/com.github.alexjlockwood.kyrie/index.html
[sample-app-source-code]: https://github.com/alexjlockwood/kyrie/tree/master/sample/src/main/java/com/example/kyrie
[adp-blog-post]: https://www.androiddesignpatterns.com/2018/03/introducing-kyrie-animated-vector-drawables.html
