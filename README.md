# Kyrie

[![Build status][travis-badge]][travis-badge-url]
[![Release](https://jitpack.io/v/alexjlockwood/kyrie.svg)](https://jitpack.io/#alexjlockwood/kyrie)

Kyrie is a superset of Android's `VectorDrawable` and `AnimatedVectorDrawable` classes: it can do everything they can do and more. 

Check out [the blog post](https://www.androiddesignpatterns.com/2018/03/introducing-kyrie-animated-vector-drawables.html) for the motivation behind the library as well as some example usages, and don't forget to read the [documentation](https://alexjlockwood.github.io/kyrie).

![Screen capture of tool](art/screencap.gif)

## Dependency

Add this to your root `build.gradle` file (*not* your module's `build.gradle` file):

```gradle
allprojects {
    repositories {
        // ...
        maven { url "https://jitpack.io" }
    }
}
```

Then add the library to your module's `build.gradle` file:

```gradle
dependencies {
    // ...
    implementation 'com.github.alexjlockwood:kyrie:latest.release.here'
}
```

The latest release version is listed at the top of this page. You can see a full list of all releases [here](https://github.com/alexjlockwood/kyrie/releases).

## Compatibility

* **Minimum Android SDK**: Kyrie requires a minimum API level of 14.
* **Compile Android SDK**: Kyrie requires you to compile against API 27 or later.

  [travis-badge]: https://travis-ci.org/alexjlockwood/kyrie.svg?branch=master
  [travis-badge-url]: https://travis-ci.org/alexjlockwood/kyrie
