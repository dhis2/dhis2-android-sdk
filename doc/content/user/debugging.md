## Debugging

<!--DHIS2-SECTION-ID:debugging-->

Besides the regular debugging tools in AndroidStudio, the library [Stetho](http://facebook.github.io/stetho/) allows the use of Chrome Developer Tools for debugging network traffic and explore the database.

Setup up Stetho by adding the following dependencies in your gradle file:

```gradle
dependencies {
    implementation 'com.facebook.stetho:stetho:1.5.0'
    implementation 'com.facebook.stetho:stetho-okhttp3:1.5.0'
}
```

Then add a network interceptor in `D2Configuration` object:

```java
D2Configuration.builder()
    ...
    .networkInterceptors(Collections.singletonList(new StethoInterceptor()))
    ...
    .build();
```

Finally enable initialize Stetho in the `Application` class:

```java
if (DEBUG) {
    Stetho.initializeWithDefaults(this);
}
```

At this point you should be able to debug the app/sdk by using Chrome Inspector Tools:

- Run a test in debug mode and set a breakpoint.
- In Chrome Browser open the [device inspector](chrome://inspect/devices#devices).
- Select the remote target and click on Inspect. A new windows will appear showing the Chrome developer tools.
- Explore database in "Resources > Web SQL".
- Explore network traffic in "Network".
