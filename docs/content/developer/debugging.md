# Debugging { #android_sdk_debugging }

## Android Studio
You can debug your DHIS2 Android App using Android Studio. Follow the [instructions from the Android Developer Portal](https://developer.android.com/studio/debug) 

## Flipper (recommended)
Flipper is a platform for debugging mobile apps on iOS and Android developed by Facebook. Flipper provides many features, 
provided by different plugins.

Specially relevant plugins to debug the SDK: 
- Network: see each request and response exchanged between the app and the web API, their data and timing
- Database: see table content and perform custom SQL queries

Steps to install it and configure it:
1. Ensure you have the Android SDK installed (you probably will if you are planning to debug an Android app)
2. Download Flipper from [its website](https://fbflipper.com/)
3. Add the diagnostic activity to the Android Manifest:

```xml
<activity android:name="com.facebook.flipper.android.diagnostics.FlipperDiagnosticActivity"
            android:exported="true"/>
```

4. Set up the plugins while configuring the SDK: 

```java
        // This will be null if not debug mode to make sure your data is safe 
        Interceptor flipperInterceptor = FlipperManager.setUp(context);

        List<Interceptor> networkInterceptors = new ArrayList<>();
        if (flipperInterceptor != null) {
            networkInterceptors.add(flipperInterceptor);
        }

        return D2Configuration.builder()
                ...
                .networkInterceptors(networkInterceptors)
                .build();
```

If you want to use any other Flipper plugins to debug other aspects of the app, we recommend you to go through [the documentation](https://fbflipper.com/docs/getting-started/android-native). 

## Stetho (legacy)
[Stetho](http://facebook.github.io/stetho/) is a sophisticated debug bridge for Android applications enabling developers 
debug their Android apps using the Chrome Developer Tools.

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

Finally initialize Stetho in the `Application` class:

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

## User ID header
Starting on the version 1.4, cookie authentication has been adopted, so basic authentication is not used in every single
request anymore. For that reason, the username is no longer sent in the HTTP request headers. As a replacement, 
we are sending the user id as plain text in a custom header `x-dhis2-user-id`.

Example: 

```
x-dhis2-user-id: DXyJmlo9rge
```