# Instructions

Increment the version numbers in `kyrie/build.gradle` and `README.md`.

Then run the following:

```sh
./gradlew clean build bintrayUpload -PbintrayUser=alexjlockwood -PbintrayKey=BINTRAY_API_KEY -PdryRun=false
```
