# Error management { #android_sdk_error_management }

Errors that happen in the context of the SDK are wrapped in a type of exception: `D2Error`, with the following fields:

| Attribute         | Type              | Optional  | Description |
|-------------------|-------------------|-----------|-------------| 
| errorComponent    | D2ErrorComponent  | true      | Source of the error: Database, SDK or Server.|
| errorCode         | D2ErrorCode       | true      | SDK-defined unique error code. |
| errorDescription  | String            | true      | Description of the error in english (technical details, just for logs and debugging). |
| httpErrorCode     | Integer           | false     | If caused by HTTP request, HTTP error code. |
| originalException | Exception         | false     | Original Java Exception causing the error, if any. |

Any operation requested to the SDK can throw an error. 

- For operations returning RxJava objects, the errors can be extracted
  in the following way:
    
    ```java
    d2.userModule().logIn(username, password, url)
        .subscribe(
            user -> { },
            error -> {
                if (error instanceof D2Error) {
                    D2Error d2Error = (D2Error) error;
                    Log.e("LOGIN", d2Error.errorComponent() + " " + d2Error.httpErrorCode() + " " + d2Error.errorCode());
                }
            }
        );
    ```

- For blocking operations, it is also possible to retrieve a `D2Error`.
  The errors can be extracted by caching them as shown in the following
  code snippet:
    
    ```java
    try {
        d2.userModule().blockingLogIn(username, password, url);
    } catch (Exception e) {
        if (e.getCause() instanceof D2Error) {
            D2Error d2Error = (D2Error) e.getCause();
            Log.e("LOGIN", d2Error.errorComponent() + " " + d2Error.httpErrorCode() + " " + d2Error.errorCode());
        }
    }
    ```

`D2Errors` are persisted in the Database when they occur, so they can be
analyzed afterwards and diagnose possible problems. They can be accessed
through it's own repository:

```java
d2.maintenanceModule().d2Errors()
    .byD2ErrorComponent().eq(D2ErrorComponent.Server)
    .get();
```

The SDK team is now working together with the core team in order to provide a full list of common error codes, but it's still a work in progress.
