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

import com.google.common.truth.Truth.assertThat
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteReadPacket
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.arch.api.HttpServiceClient.Companion.IsAbsouteUrlHeader
import org.hisp.dhis.android.core.arch.api.HttpServiceClient.Companion.IsExternalRequestHeader
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RequestBuilderShould {

    private lateinit var mockEngine: MockEngine
    private lateinit var client: HttpClient
    private lateinit var service: HttpServiceClient

    @Before
    fun setUp() {
        mockEngine = MockEngine { request ->

            respond(
                content = ByteReadChannel("OK"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString()),
            )
        }
        client = HttpClient(mockEngine)
        service = HttpServiceClient(client)
    }

    @Test
    fun build_url_correctly() = runTest {
        val mockEngine = MockEngine { request ->
            assertEquals("https://temporary-dhis-url.org/api/test", request.url.toString())
            assertThat(request.headers.contains(IsAbsouteUrlHeader)).isFalse()

            respondOk()
        }

        val client = HttpClient(mockEngine)
        val service = HttpServiceClient(client)

        service.get<String> {
            url("test")
        }
    }

    @Test
    fun build_internal_absoluteUrl_correctly() = runTest {
        val absoluteUrl = "https://dummy-absolute-url.org/api/test"
        val mockEngine = MockEngine { request ->
            assertEquals(absoluteUrl, request.url.toString())
            assertEquals("true", request.headers.get(IsAbsouteUrlHeader))
            assertEquals(false, request.headers.contains(IsExternalRequestHeader))

            respondOk()
        }

        val client = HttpClient(mockEngine)
        val service = HttpServiceClient(client)

        service.get<String> {
            absoluteUrl(absoluteUrl, false)
        }
    }

    @Test
    fun build_external_absoluteUrl_correctly() = runTest {
        val absoluteUrl = "https://dummy-absolute-url.org/api/test"
        val mockEngine = MockEngine { request ->
            assertEquals(absoluteUrl, request.url.toString())
            assertEquals("true", request.headers[IsAbsouteUrlHeader])
            assertEquals("true", request.headers[IsExternalRequestHeader])

            respondOk()
        }

        val client = HttpClient(mockEngine)
        val service = HttpServiceClient(client)

        service.get<String> {
            absoluteUrl(absoluteUrl)
        }
    }

    @Test
    fun build_authorization_header() = runTest {
        val mockEngine = MockEngine { request ->
            assertEquals("Bearer token_value", request.headers[HttpHeaders.Authorization])

            respondOk()
        }

        val client = HttpClient(mockEngine)
        val service = HttpServiceClient(client)

        service.get<String> {
            url("test")
            authorizationHeader("Bearer token_value")
        }
    }

    @Test
    fun build_request_with_custom_headers() = runTest {
        val mockEngine = MockEngine { request ->
            assertEquals("value1", request.headers["header1"])
            assertEquals("value2", request.headers["header2"])

            respondOk()
        }

        val client = HttpClient(mockEngine)
        val service = HttpServiceClient(client)

        service.get<String> {
            url("test")
            header("header1", "value1")
            header("header2", "value2")
        }
    }

    @Test
    fun build_POST_request_with_JSON_body_correctly() = runTest {
        val mockEngine = MockEngine { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals(ContentType.Application.Json, request.body.contentType)
            assertEquals("""{"key": "value"}""", request.body.toByteReadPacket().readText())

            respondOk()
        }

        val client = HttpClient(mockEngine)
        val service = HttpServiceClient(client)

        service.post<String> {
            url("test")
            body("""{"key": "value"}""")
        }
    }

    @Test
    fun build_POST_request_with_Multipart_body_correctly() = runTest {
        val mockEngine = MockEngine { request ->

            assertEquals(HttpMethod.Post, request.method)
            assertThat(request.body.contentType.toString().contains("multipart/form-data"))

            respondOk()
        }

        val client = HttpClient(mockEngine)
        val service = HttpServiceClient(client)

        service.post<String> {
            url("upload")
            body(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            "test content".toByteArray(),
                            Headers.build {
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
    }

    @Test
    fun build_query_parameters() = runTest {
        val paging = true
        val page = 2
        val pageSize = 15
        val mockEngine = MockEngine { request ->
            assertEquals(
                "https://temporary-dhis-url.org/api/test" +
                    "?param1=value1" +
                    "&param2=value2" +
                    "&listParam=value3" +
                    "&listParam=value4" +
                    "&paging=true" +
                    "&page=2" +
                    "&pageSize=15",
                request.url.toString(),
            )

            respondOk()
        }

        val client = HttpClient(mockEngine)
        val service = HttpServiceClient(client)

        service.get<String> {
            url("test")
            parameters {
                attribute("param1" to "value1")
                attribute("param2" to "value2")
                attribute("listParam" to listOf("value3", "value4"))

                paging(paging)
                page(page)
                pageSize(pageSize)
            }
        }
    }

    fun MockRequestHandleScope.respondOk(content: String = "OK") = respond(
        content = content,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString()),
        status = HttpStatusCode.OK,
    )
}
