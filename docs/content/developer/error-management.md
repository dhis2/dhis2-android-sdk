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

- For `suspend` operations, the error is thrown directly and can be caught as usual:

    ```kotlin
    try {
        d2.userModule().suspendLogIn(username, password, url)
    } catch (d2Error: D2Error) {
        Log.e("LOGIN", "${d2Error.errorComponent()} ${d2Error.httpErrorCode()} ${d2Error.errorCode()}")
    }
    ```

- For operations returning RxJava objects, the errors can be extracted
  in the following way:
    
    ```kotlin
    d2.userModule().rxLogIn(username, password, url)
        .subscribe(
            { user -> },
            { error ->
                if (error is D2Error) {
                    Log.e("LOGIN", "${error.errorComponent()} ${error.httpErrorCode()} ${error.errorCode()}")
                }
            },
        )
    ```

- For blocking operations, it is also possible to retrieve a `D2Error`.
  The errors can be extracted by catching them as shown in the following
  code snippet:
    
    ```kotlin
    try {
        d2.userModule().blockingLogIn(username, password, url)
    } catch (e: Exception) {
        val cause = e.cause
        if (cause is D2Error) {
            Log.e("LOGIN", "${cause.errorComponent()} ${cause.httpErrorCode()} ${cause.errorCode()}")
        }
    }
    ```

`D2Errors` are persisted in the Database when they occur, so they can be
analyzed afterwards and diagnose possible problems. They can be accessed
through it's own repository:

```kotlin
d2.maintenanceModule().d2Errors()
    .byD2ErrorComponent().eq(D2ErrorComponent.Server)
    .suspendGet()
```

The SDK team is now working together with the core team in order to provide a full list of common error codes, but it's still a work in progress.
