# dhis2-android-sdk
[![Build Status](https://travis-ci.org/dhis2/dhis2-android-sdk.svg?branch=master)](https://travis-ci.org/dhis2/dhis2-android-sdk)

This project is in alpha mode. You are free to experiment with this project, but please give feedback through issues if things are not working properly. We are planning to have a stable beta version of this project by 1st June 2016 

Include this project in your Android application project to take advantage of existing implementations of components that may be common for DHIS 2 Android apps!

Please note that this is a library project, and not a standalone application.

Do also note that this is an implementation of the generic [DHIS 2 Java SDK](https://github.com/dhis2/dhis2-sdk-java). This is only an extension of the Java Client library for DHIS 2 that is the DHIS 2 Java SDK where platform specific aspects have been implemented for Android (storage and network interfaces). To read about the details and the use of the SDK, you should therefore visit the [Wiki](https://github.com/dhis2/dhis2-sdk-java/wiki/Client-SDK-for-DHIS-2-in-Java) pages of the DHIS 2 Java SDK

However, briefly it can be said that the SDK includes:
+ Representations of data models such as Data Elements, Events, etc.
+ Existing implementations for connection to DHIS 2 server for retrieving data and sending data.
+ Local persistence for offline support.

The SDK requires functionality in the Web API added in the release of 2.21, so DHIS 2 versions 2.20 and lower will experience problems.

##Getting Started:
In this section it is briefly explained how you can get started by logging in, loading assigned programs to device, and loading it from local storage.

###Working with RX:
The SDK uses RxJava for asynchronous loading, which we encourage you to use, but if you don't want to it can simply be avoided. An example of loading Programs from local persistence into memory either using it or not using it follows:
####Using RxJava:
```java
Observable<List<Program>> programObservable = D2.programs().list();
        programObservable.subscribe(new Action1<List<Program>>() {
            @Override
            public void call(List<Program> programs) {
                //do something
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //handle error
            }
        });
  ```
        
####Not using RxJava:
  ```java
  List<Program> programs = D2.programs().list().toBlocking().first();
  ```

###Logging in:
```java
Configuration configuration = new Configuration(serverUrl);
Observable<UserAccount> observable = D2.signIn(configuration, username, password);
        observable.subscribe(new Action1<UserAccount>() {
            @Override
            public void call(UserAccount userAccount) {
                // do something
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                // do something
            }
        });
  ```

###Synchronizing Assigned Programs onto device:
After you have successfully logged in, you can simply load Assigned Programs by calling:
```java
D2.me().syncAssignedPrograms();
```

This will load the assigned programs according to your user on the provided server, and save in local storage.

###Synchronizing Organisation Units onto device:
The method for synchronizing Assigned Programs above will synchronize all Organisation Units that have Programs assigned to them, but if you want to load all Organisation Units onto your device, you can call:
```java
D2.organisationUnits().sync();
```

Loading Programs from local persistence into memory:
After you have successfully synchronized programs from the server onto the device, you can load it from persistence by calling:
```java
Observable<List<Program>> programObservable = D2.programs().list();
        programObservable.subscribe(new Action1<List<Program>>() {
            @Override
            public void call(List<Program> programs) {
                //do something
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //handle error
            }
        });
  ```
