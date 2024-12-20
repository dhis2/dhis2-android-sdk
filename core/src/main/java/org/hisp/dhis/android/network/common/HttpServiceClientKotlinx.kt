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

package org.hisp.dhis.android.network.common

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import okhttp3.OkHttpClient
import org.hisp.dhis.android.core.D2Configuration
import org.hisp.dhis.android.core.arch.api.RequestBuilder
import org.hisp.dhis.android.core.arch.api.internal.addKtorPlugins
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.koin.core.annotation.Singleton

@Singleton
internal class HttpServiceClientKotlinx(
    val okHttpClient: OkHttpClient,
    val d2Configuration: D2Configuration,
    val client: HttpClient = HttpClient(OkHttp) {
        engine {
            preconfigured = okHttpClient
        }
        install(ContentNegotiation) {
            json(KotlinxJsonParser.instance)
        }
        expectSuccess = true
        followRedirects = false
        addKtorPlugins(d2Configuration)
    },
    internal var baseUrl: String = "https://temporary-dhis-url.org/api/",
) {
    internal suspend inline fun <reified T> request(
        requestMethod: HttpMethod,
        block: RequestBuilder.() -> Unit,
    ): T {
        val requestBuilder = RequestBuilder(baseUrl).apply(block)
        val response: HttpResponse = client.request(requestBuilder.url) {
            method = requestMethod
            url {
                requestBuilder.parameters.forEach { (key, value) ->
                    parameters.append(key, value)
                }
            }
            addHeaders(requestBuilder)
            if (method == HttpMethod.Post || method == HttpMethod.Put) {
                when (requestBuilder.body) {
                    is MultiPartFormDataContent -> contentType(ContentType.MultiPart.FormData)
                    else -> contentType(ContentType.Application.Json)
                }
                setBody(requestBuilder.body)
            }
        }
        return if (T::class == ByteArray::class) {
            response.readBytes() as T
        } else {
            response.body()
        }
    }

    internal fun HttpRequestBuilder.addHeaders(requestBuilder: RequestBuilder) {
        requestBuilder.headers.forEach { (key, value) ->
            header(key, value)
        }
        if (requestBuilder.isAbsoluteUrl) {
            header(IS_ABSOLUTE_URL_HEADER, true)
        }
        if (requestBuilder.isExternalRequest) {
            header(IS_EXTERNAL_REQUEST_HEADER, true)
        }
        requestBuilder.authorizationHeader?.let {
            header(HttpHeaders.Authorization, it)
        }
    }

    suspend inline fun <reified T> get(block: RequestBuilder.() -> Unit): T {
        return request(HttpMethod.Get, block)
    }

    suspend inline fun <reified T> post(block: RequestBuilder.() -> Unit): T {
        return request(HttpMethod.Post, block)
    }

    suspend inline fun <reified T> put(block: RequestBuilder.() -> Unit): T {
        return request(HttpMethod.Put, block)
    }

    suspend inline fun <reified T> delete(block: RequestBuilder.() -> Unit): T {
        return request(HttpMethod.Delete, block)
    }

    companion object {
        internal const val IS_ABSOLUTE_URL_HEADER = "isAbsoluteUrl"
        internal const val IS_EXTERNAL_REQUEST_HEADER = "isExternalRequest"
    }
}
