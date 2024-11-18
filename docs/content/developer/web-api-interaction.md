# Web API Interaction { #android_sdk_web_api_interaction }

When using the SDK sometimes one would like to make requests that are not supported by the default sdk, but fortunately the SDK offers another approach allowing you to interact with the Web API and here are the steps to follow.

## HttpServiceClient

The SDK uses `HttpServiceClient` based on Ktor for HTTP requests to the Web API. You can define custom services to interact with various endpoints using this client. The client object is available through the d2 instance.

> **Important**
>
> The end-point must be an end-point supported by the Web API otherwise your request will not succeed.

```kotlin
class CustomService(private val client: HttpServiceClient) {

    suspend fun getData(
        path: String,
        field: String,
        filter: String,
        paging: Boolean,
        page: Int?,
        pageSize: Int?,
    ): User {
        return client.get {
            url("end-point/$path")
            parameters{
                attribute("field", field)
                attribute("filter", filter)
                paging(paging)
                page(page)
                pageSize(pageSize)
            }
        }
    }

    suspend fun postData(
        body: BodyClass
    ): Body {
        return client.post {
            url("some_endpoint")
            setBody(body)
        }
    }
}
```

## Using the service

Instantiate your service and perform requests:


```kotlin
val customService = CustomService(httpServiceClient)

// Get data
val data = customService.getData(
    "dataPath",
    "dataField",
    "dataFielter",
    false,
)

// Post data
val dataBody = BodyClass(data = "some data")
val createdData = customService.postData(dataBody)
```

> **Important**
>
> This httpServiceClient approach is asynchronous and leverages Kotlin coroutines for managing requests and responses.

## Notes
- **Error Handling**: Make sure to handle potential errors in your service methods.
- **Request DSL**: The approach for defining requests uses a DSL provided by `RequestBuilder` to build the request.
- **Based on Ktor**: `httpServiceClient` uses Ktor's `HttpClient` for making HTTP requests.

