# Development

<!--DHIS2-SECTION-ID:development-->

## Build

There is `build.sh` script that builds the project, runs the test suite and perform additional code checks (pmd, checkstyle, findbugs). It takes longer than routine builds, but it is recommended to execute it before creating a PR because it is used by the CI platform (Travis) to validate the PRs.

## Testing

The SDK follows the standard test structure: unit tests (`test` folder) and instrumented unit test (`androidTest` folder).

There is third kind of tests, usually called `*RealIntegratinoShould`, which usually connect to real DHIS2 servers and create databases in the device. They are very useful to test against a real DHIS2 API. These tests are commented out by default and are not executed in CI builds because they rely on a particular server configuration and availability. To execute these tests:

- Remove the comment on `@Test` annotation.
- Optionally define a database name in `AbsStoreCase`. In this way data will be persisted in the device or emulator and could be browsed.
- Check the instance url in D2 initialization. Also check the username and password for login.

## Debugging

Besides the regular debugging tools in AndroidStudio, the library [Stetho](http://facebook.github.io/stetho/) allows the use of Chrome Developer Tools for debugging network traffic and explore the database.

Setup up Stetho by adding the following dependencies in your gradle file:

```
dependencies {
    implementation 'com.facebook.stetho:stetho:1.5.0'
    implementation 'com.facebook.stetho:stetho-okhttp3:1.5.0'
}
```

Then add a network interceptor in `D2Configuration` object:

```
D2Configuration.builder()
    ...
    .networkInterceptors(Collections.singletonList(new StethoInterceptor()))
    ...
    .build();
```

At this point you should be able to debug the app/sdk by using Chrome Inspector Tools:

- Run a test in debug mode and set a breakpoint.
- In Chrome Browser open the [device inspector](chrome://inspect/devices#devices).
- Select the remote target and click on Inspect. A new windows will appear showing the Chrome developer tools.
- Explore database in "Resources > Web SQL".
- Explore network traffic in "Network".
