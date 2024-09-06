/*
 *  Copyright (c) 2004-2024, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.arch.api.httpservice.internal

import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

data class MyResponse(
    @JsonProperty("message") val message: String,
    @JsonProperty("id") val id: Int,
)

class HttpServiceClientShould {

    private lateinit var mockEngine: MockEngine
    private lateinit var client: HttpClient
    private lateinit var service: HttpServiceClient

    @Before
    fun setUp() {
        mockEngine = MockEngine { request ->
            respond(
                content = ByteReadChannel("""{"message": "OK", "id": 123}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        buildHttpServiceClient(mockEngine)
    }

    private fun buildHttpServiceClient(mockEngine: MockEngine) {
        client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                val converter = JacksonConverter(ObjectMapperFactory.objectMapper(), true)
                register(ContentType.Application.Json, converter)
            }
        }
        service = HttpServiceClient(client)
    }

    @Test
    fun return_valid_GET_request_response() = runTest {
        val response: MyResponse = service.get {
            url("https://temporary-dhis-url.org/api/test")
        }
        assertEquals("OK", response.message)
        assertEquals(123, response.id)
    }

    @Test
    fun send_json_body_and_return_valid_POST_request_response() = runTest {
        val jsonBody = """{"key": "value"}"""

        val response: MyResponse = service.post {
            url("https://temporary-dhis-url.org/api/test")
            body(jsonBody)
        }
        assertEquals("OK", response.message)
    }

    @Test
    fun send_multipart_body_and_return_valid_POST_request_response() = runTest {
        val response: MyResponse = service.post {
            url("https://temporary-dhis-url.org/api/upload")
            body(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "file",
                            value = "test".toByteArray(),
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; name=\"file\"; filename=\"test.txt\"",
                                )
                            },
                        )
                    },
                ),
            )
        }
        assertEquals("OK", response.message)
    }

    @Test
    fun send_json_body_and_return_valid_PUT_request_response() = runTest {
        val jsonBody = """{"key": "value"}"""

        val response: MyResponse = service.put {
            url("https://temporary-dhis-url.org/api/test")
            body(jsonBody)
        }
        assertEquals("OK", response.message)
    }

    @Test
    fun return_valid_DELETE_request_response() = runTest {
        val response: MyResponse = service.delete {
            url("https://temporary-dhis-url.org/api/test")
        }
        assertEquals("OK", response.message)
    }

    @Test
    fun return_valid_GET_resource_request_response() = runTest {
        val fileResourcemockEngine = MockEngine { request ->
            respond(
                content = ByteReadChannel("Raw body content".toByteArray()),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.OctetStream.toString()),
            )
        }
        buildHttpServiceClient(fileResourcemockEngine)

        val response: ResponseBody = service.get {
            url("fileResourceSite")
        }

        assertEquals("Raw body content", response.string())
    }
}
