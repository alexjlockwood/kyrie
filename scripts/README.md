# Instructions

Increment the version numbers in `kyrie/build.gradle` and `kotlin-dsl/build.gradle`. Then run the following:

```
./gradlew kyrie:install
./gradlew kyrie:bintrayUpload
./gradlew kotlin-dsl:install
./gradlew kotlin:bintrayUpload
```
