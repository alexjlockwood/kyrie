# Kyrie

[![Build status][travis-badge]][travis-badge-url]
[![Release](https://jitpack.io/v/alexjlockwood/kyrie.svg)](https://jitpack.io/#alexjlockwood/kyrie)

![Screen capture of tool](art/screencap.gif)

## Dependency

Add this to your root `build.gradle` file (*not* your module's `build.gradle` file):

```
allprojects {
    repositories {
        // ...
        maven { url "https://jitpack.io" }
    }
}
```

Then add the library to your module's `build.gradle` file:

```
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
