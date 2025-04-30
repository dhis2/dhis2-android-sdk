# Web API Interaction { #android_sdk_web_api_interaction }

When using the SDK sometimes one would like to make requests that are not supported by the default sdk, but fortunately the SDK offers another approach allowing you to interact with the Web API and here are the steps to follow.

## HttpServiceClient

The SDK uses `HttpServiceClient` based on Ktor for HTTP requests to the Web API. You can define custom services to interact with various endpoints using this client. The client object is available through the d2 instance.

In version 1.12, Kotlinx Serialization was introduced to manage de/serialization of json objects. For this to properly work, a class annotated with `@Serializable` must be used. See UserDTO example below. 

More information about serialization can be found in the [Kotlin documentation](https://kotlinlang.org/docs/serialization.html) and [Ktor documentation](https://ktor.io/docs/client-serialization.html)

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
    ): UserDTO {
        return client.get {
            url("get-endpoint-url/$path")
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
        body: UserDTO
    ): ResponseDTO {
        return client.post {
            url("post-endpoint")
            setBody(body)
        }
    }
}

@Serializable
data class UserDTO(
    val name: String,
    val surname: String,
    val age: Int,
)

@Serializable
data class ResponseDTO(
    val status: String,
    val responseMessage: String,
)
```

## Using the service

Instantiate your service and perform requests:


```kotlin
val customService = CustomService(httpServiceClient)

// Get data
val userDTO = customService.getData(
    "dataPath",
    "dataField",
    "dataFielter",
    false,
)

// Post data
val dataBody = UserDTO(name = "Name", surname = "Surname", age = 20)
val responseDTO = customService.postData(dataBody)
```

> **Important**
>
> This httpServiceClient approach is asynchronous and leverages Kotlin coroutines for managing requests and responses.

## Notes
- **Error Handling**: Make sure to handle potential errors in your service methods.
- **Request DSL**: The approach for defining requests uses a DSL provided by `RequestBuilder` to build the request.
- **Based on Ktor**: `httpServiceClient` uses Ktor's `HttpClient` for making HTTP requests.

