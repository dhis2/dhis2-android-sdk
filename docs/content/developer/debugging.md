# Debugging { #android_sdk_debugging }

## Android Studio
You can debug your DHIS2 Android App using Android Studio. Follow the [instructions from the Android Developer Portal](https://developer.android.com/studio/debug)

## HTTP Toolkit
Using HTTP Toolkit you can explore and mock the HTTP requests when needed. It is easy to install and use ([HTTP toolkit page](https://httptoolkit.com)).

It can be easily attached to emulators and rooted devices; in the case of non-rooted physical devices, it requires additional steps.

## User ID header
Starting on the version 1.4, cookie authentication has been adopted, so basic authentication is not used in every single
request anymore. For that reason, the username is no longer sent in the HTTP request headers. As a replacement, 
we are sending the user id as plain text in a custom header `x-dhis2-user-id`.

Example: 

```
x-dhis2-user-id: DXyJmlo9rge
```