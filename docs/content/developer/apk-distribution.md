# APK distribution { #android_sdk_apk_distribution }
The web app [APK distribution](https://apps.dhis2.org/app/dff273fc-909e-48af-b151-c4d7e9c8a12c) is used to determine the APK that must be used by the Android app. It is a way to control the version of the Android apps.

This parameter is automatically used by the official Android Capture app, which will offer the update when a new version is identified. Other custom applications might use this parameter to control their own updates. This logic must be implemented at application level.

The APK distribution app allows the definition of different versions by user group. The SDK will internally handle this logic and determine the version that must be used by the current user.

The app can get this version by using the LatestAppVersion repository:

```kt
d2.settingModule().latestAppVersion().get()
```

This version is updated with each metadata sync. To check for updates without triggering a full metadata sync, this method can be used:

```kt
d2.settingModule().latestAppVersion().download()
```

Once the download is completed, the version can be read from the database as usual.

Check more information about the app in the [docs](https://docs.dhis2.org/en/use/android-app/apk-distribution.html).