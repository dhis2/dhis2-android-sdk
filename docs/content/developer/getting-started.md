# Getting started

<!--DHIS2-SECTION-ID:getting_started-->

## Installation

<!--DHIS2-SECTION-ID:installation-->

Include dependency in build.gradle.

```gradle
dependencies {
    implementation "org.hisp.dhis:android-core:1.3.0"
    ...
}
```

Additionally, you need to include this repository in your root gradle file if it is not already there:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

## D2 initialization

<!--TODO-->

<!--DHIS2-SECTION-ID:initialization-->

In order to start using the SDK, the first step is to initialize a `D2` object. The helper class `D2Manager` offers static methods to setup and initialize the `D2` instance. Also, it ensures that `D2` is a singleton across the application.

The minimum configuration that needs to be passed to the `D2Manager` is the following: 

```java
D2Configuration configuration = D2Configuration.builder()
    .context(context)
    .build();
```

Using the configuration you can instantiate `D2`.

```java
Single<D2> d2Single = D2Manager.instantiateD2(configuration);
```

Once the Single is completed, you can access D2 with the following method:

```java
D2 d2 = D2Manager.getD2();
```

If you are not using RxJava, you can instantiate `D2` in a blocking way:

```java
D2 d2 = D2Manager.blockingInstantiateD2(configuration);
```

The object `D2Configuration` has a lot of fields to configure the behavior of the SDK.

|  Attribute    |   Required    |   Description | Default
|-|-|-|-|
| context       | true          | Application context | -
| appName       | false         | Use to create the "user-agent" header | From Android Manifest
| appVersion    | false         | Use to create the "user-agent" header | From Android Manifest
| readTimeoutInSeconds | false  | Read timeout for http queries | 30 seconds
| connectTimeoutInSeconds | false | Connect timeout for http queries | 30 seconds
| writeTimeoutInSeconds | false | Write timeout for http queries | 30 seconds
| interceptors  | false         | Interceptors for OkHttpClient | None
| networkInterceptors | false   | NetworkInterceptors for OkHttpClient | None
