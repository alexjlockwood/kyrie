# Instructions

Increment the version numbers in `kyrie/build.gradle`, `kyrie-kotlin-dsl/build.gradle`, and `README.md`.

Then run the following:

```
./gradlew kyrie:install
./gradlew kyrie:bintrayUpload
./gradlew kyrie-kotlin-dsl:install
./gradlew kyrie-kotlin:bintrayUpload
```
