# dhis2-android-sdk
[![Build Status](https://travis-ci.org/dhis2/dhis2-android-sdk.svg?branch=master)](https://travis-ci.org/dhis2/dhis2-android-sdk)

Include this project in your Android application project to take advantage of existing implementations of components that may be common for DHIS 2 Android apps.

Please note that this is a library project, and not a standalone application.

The SDK includes:
+ Representations of data models such as Data Elements, Events, etc.
+ Existing implementations for connection to DHIS 2 server for retrieving data and sending data.
+ Local persistence for offline support.
+ Services for automatic background synchronization with server.
+ Reusable User Interface elements such as widgets and Login Activity. 

The SDK is still in beta phase but feel free to experiment with it. See example projects https://github.com/dhis2/dhis2-android-trackercapture and https://github.com/dhis2/dhis2-android-eventcapture on how to set up.

The SDK requires functionality in the Web API added to trunk and is currently targeting the upcoming release of 2.20, so DHIS 2 versions 2.19 and lower will experience problems.
