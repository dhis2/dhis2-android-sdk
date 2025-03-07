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

package org.hisp.dhis.android.core.arch.api.authentication.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import io.ktor.client.call.HttpClientCall
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HeadersImpl
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.InternalAPI
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import org.junit.Test
import kotlin.coroutines.CoroutineContext

class CookieAuthenticatorShould {

    @InternalAPI
    @Test
    fun store_multiple_cookies() {
        val cookieHelper = CookieAuthenticatorHelper()

        val response = object : HttpResponse() {
            override val call: HttpClientCall = mock()
            override val content: ByteReadChannel = mock()
            override val coroutineContext: CoroutineContext = mock()
            override val requestTime: GMTDate = GMTDate.START
            override val responseTime: GMTDate = GMTDate.START
            override val status: HttpStatusCode = HttpStatusCode.OK
            override val version: HttpProtocolVersion = HttpProtocolVersion.HTTP_2_0
            override val headers: Headers = HeadersImpl(
                mapOf(
                    "set-cookie" to listOf(
                        "JSESSIONID=4DD96301F71D2F5EC41DFD1D3BC012AB; Path=/current; Secure; HttpOnly",
                        "_ga=34FJALK23LLFLF; Secure; HttpOnly"
                    )
                )
            )
        }

        cookieHelper.storeCookieIfSentByServer(response)

        val requestBuilder = HttpRequestBuilder()
        cookieHelper.addCookieHeader(requestBuilder)

        assertThat(requestBuilder.headers["Cookie"])
            .isEqualTo("JSESSIONID=4DD96301F71D2F5EC41DFD1D3BC012AB; _ga=34FJALK23LLFLF")
    }
}
