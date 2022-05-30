# Web API Interaction { #android_sdk_web_api_interaction }

When using the SDK sometimes one would like to make requests that are not supported by the default sdk, but fortunately the SDK offers another approach allowing you to interact with the Web API and here are the steps to follow.

## Retrofit Service

The SDK uses Retrofit to make requests to the Web API and the first thing we have to do is to create a retrofit service by specifying a particular end-point. Everything you already know about [Retrofit](https://square.github.io/retrofit/) can be more or less used here.

> **Important**
>
> The end-point must be an end-point supported by the Web API otherwise your request will not succeed.

```java
interface MyService {

    @GET("/end-point")
    Single<Payload<User>> getSomeThing();
}

```

## Query

The service we have created in the previous section will help us to make HTTP requests to the server, but to do that we need an instance of the service (the MyService interface we created previously), an instance of the service ca be created using the Retrofit instance that is already available in the SDK and we can have access to it as shown in the following code snippet

```java
MyService myService = d2().retrofit().create(MyService.class);
Single<Payload<User>> userPayload = myService.getSomeThing();
```

As we have used `Single` as the return type we can get the query response in a blocking way.

```java
Payload<User> userPayload = myService.getSomeThing().blockingGet();
```

> **Important**
>
> The `Single` class come from [RxJava](https://github.com/ReactiveX/RxJava) that's already included in the SDK