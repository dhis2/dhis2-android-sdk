# dhis2-android-sdk
[![Build Status](https://travis-ci.org/dhis2/dhis2-android-sdk.svg?branch=master)](https://travis-ci.org/dhis2/dhis2-android-sdk)

Include this project in your Android application project to take advantage of existing implementations of components that may be common for DHIS 2 Android apps!

Please note that this is a library project, and not a standalone application.

Do also note that this is an implementation of the generic [DHIS 2 Java SDK](https://github.com/dhis2/dhis2-sdk-java). This is only an extension of the Java Client library for DHIS 2 that is the DHIS 2 Java SDK where platform specific aspects have been implemented for Android (storage and network interfaces). To read about the details and the use of the SDK, you should therefore visit the [Wiki](https://github.com/dhis2/dhis2-sdk-java/wiki/Client-SDK-for-DHIS-2-in-Java) pages of the DHIS 2 Java SDK

However, briefly it can be said that the SDK includes:
+ Representations of data models such as Data Elements, Events, etc.
+ Existing implementations for connection to DHIS 2 server for retrieving data and sending data.
+ Local persistence for offline support.

The SDK requires functionality in the Web API added in the release of 2.21, so DHIS 2 versions 2.20 and lower will experience problems.
