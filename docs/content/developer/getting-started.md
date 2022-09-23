# Getting started { #android_sdk_getting_started }

## Installation { #android_sdk_installation }

Include dependency in build.gradle.

```gradle
dependencies {
    implementation "org.hisp.dhis:android-core:1.7.0"
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

Check [compatibility here](#android_sdk_compatibility).

## D2 initialization { #android_sdk_initialization }

<!--TODO-->

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

## Google Play Services Support

The Google play services have been completely removed to make the SDK work on devices where the play services are not allowed; the SDK was using the `ProviderInstaller` present in the play service dependency to update the security provider to be protected against SSL exploits

If you're using a SDK version without google play services support, you have two options to update the security provider against SSL exploits, you can use an open-source solution called [conscrypt](https://github.com/google/conscrypt) to target devices without the google play services support or you can use [play-services-safetynet](https://developer.android.com/training/articles/security-gms-provider) dependency that's not open-source and required the google play services.

### 1. Play services Safetynet Dependency

If you're targeting devices that support the google play services, the best option is to use the `ProviderInstaller` included in the play-services-SafetyNet dependency by following steps.

```gradle
dependencies {
    implementation "com.google.android.gms:play-services-safetynet:<version>"
    ...
}
```

After adding the `play-services-safetynet` dependency just create a method that you can used to install the security provider

```java
public static void initialize(Context context){
    try {
        // ....
        ProviderInstaller.installIfNeeded(context.getApplicationContext());
        // ....
    } catch (GooglePlayServicesRepairableException e) {
        Log.e(TAG, e.toString());
    } catch (GooglePlayServicesNotAvailableException e) {
        Log.e(TAG, e.toString());
    } catch (NoSuchAlgorithmException e) {
        Log.e(TAG, e.toString());
    }
}
```

For more information about `play-services-safetynet`, check the official documentation [link](https://developer.android.com/training/articles/security-gms-provider)

### 2. Open-source solution

If you're not targeting devices with the google play services, [conscrypt](https://github.com/google/conscrypt) is an alternative to the `play-services-safetynet` dependency that can be used to update security provider against SSL exploits

```gradle
dependencies {
    implementation 'org.conscrypt:conscrypt-android:<conscrypt-version>'
    ...
}
```

Setting up conscrypt is similar to the Google’s provider like in the following code :

```java
public static void initialize(){
    try {
        Security.insertProviderAt(Conscrypt.newProvider(), 1);
    } catch(Exception e) {
        Log.e(TAG, e.toString());
    } catch (NoSuchAlgorithmException e) {
        Log.e(TAG, e.toString());
    }
}
```

For more information about conscrypt, check the official github repository on this [link](https://github.com/google/conscrypt)


