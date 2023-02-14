# Coverage report

The DHIS2 Android SDK uses JaCoCo to build the coverage report. The JaCoCo plugin configuration is located in the "plugins folder".

The coverage report has no explicit dependencies, so unitTests and AndroidTests should be run in first place.

How to generate the report:

```bash
// Run unit tests. It generates a coverage report (.exec) in build/jacoco.
./gradlew testDebugUnitTest

// Run android test with coverage. This property is used in build.gradle to activate the coverage report.
// It generates a coverage report (.ec) in build/outputs/code_coverage.
./gradlew -Pcoverage connectedDebugAndroidTest

// Generate the combined report
./gradlew jacocoReport
```

Configuration based on this tutorial: https://proandroiddev.com/android-code-coverage-on-firebase-test-lab-part-1-the-basics-9e1492ec5399.
