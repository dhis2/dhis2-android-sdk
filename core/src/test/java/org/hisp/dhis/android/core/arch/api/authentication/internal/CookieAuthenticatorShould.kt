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
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CookieAuthenticatorShould {

    private val host = "play.im.dhis2.org"
    private val otherHost = "another.dhis2.org"
    private val instanceAPath = "/stable-2-41-9"
    private val instanceBPath = "/stable-2-43-0-1"

    private fun clientRespondingWithCookies(cookies: List<String>): HttpClient {
        return HttpClient(
            MockEngine { _ ->
                respond(
                    content = "OK",
                    headers = Headers.build {
                        cookies.forEach { append("set-cookie", it) }
                    },
                )
            },
        )
    }

    private suspend fun storeCookiesFrom(cookieHelper: CookieAuthenticatorHelper, host: String, cookies: List<String>) {
        val response: HttpResponse = clientRespondingWithCookies(cookies).get("https://$host/api/me")
        cookieHelper.storeCookieIfSentByServer(response)
    }

    private fun requestTo(host: String, instancePath: String): HttpRequestBuilder {
        return HttpRequestBuilder().apply { url("https://$host$instancePath/api/metadata") }
    }

    @Test
    fun store_and_send_cookies_matching_the_request_path() = runTest {
        val cookieHelper = CookieAuthenticatorHelper()

        storeCookiesFrom(
            cookieHelper,
            host,
            listOf(
                "JSESSIONID=4DD96301F71D2F5EC41DFD1D3BC012AB; Path=$instanceAPath; Secure; HttpOnly",
                "_ga=34FJALK23LLFLF; Secure; HttpOnly",
            ),
        )

        val requestBuilder = requestTo(host, instanceAPath)
        cookieHelper.addCookieHeader(requestBuilder)

        assertThat(requestBuilder.headers["Cookie"])
            .isEqualTo("JSESSIONID=4DD96301F71D2F5EC41DFD1D3BC012AB; _ga=34FJALK23LLFLF")
    }

    @Test
    fun not_mix_cookies_between_instances_sharing_the_same_host() = runTest {
        val cookieHelper = CookieAuthenticatorHelper()

        storeCookiesFrom(cookieHelper, host, listOf("JSESSIONID=INSTANCE_A_SESSION; Path=$instanceAPath"))

        assertThat(cookieHelper.isCookieDefined(requestTo(host, instanceAPath))).isTrue()
        assertThat(cookieHelper.isCookieDefined(requestTo(host, instanceBPath))).isFalse()

        val requestToOtherInstance = requestTo(host, instanceBPath)
        cookieHelper.addCookieHeader(requestToOtherInstance)

        assertThat(requestToOtherInstance.headers["Cookie"]).isNull()
    }

    @Test
    fun not_mix_cookies_between_hosts() = runTest {
        val cookieHelper = CookieAuthenticatorHelper()

        storeCookiesFrom(cookieHelper, host, listOf("JSESSIONID=INSTANCE_A_SESSION; Path=$instanceAPath"))

        assertThat(cookieHelper.isCookieDefined(requestTo(otherHost, instanceAPath))).isFalse()

        val requestToOtherHost = requestTo(otherHost, instanceAPath)
        cookieHelper.addCookieHeader(requestToOtherHost)

        assertThat(requestToOtherHost.headers["Cookie"]).isNull()
    }

    @Test
    fun remove_cookies_only_for_the_matching_instance() = runTest {
        val cookieHelper = CookieAuthenticatorHelper()
        storeCookiesFrom(cookieHelper, host, listOf("JSESSIONID=INSTANCE_A_SESSION; Path=$instanceAPath"))
        storeCookiesFrom(cookieHelper, host, listOf("JSESSIONID=INSTANCE_B_SESSION; Path=$instanceBPath"))

        cookieHelper.removeCookie(requestTo(host, instanceAPath))

        assertThat(cookieHelper.isCookieDefined(requestTo(host, instanceAPath))).isFalse()
        assertThat(cookieHelper.isCookieDefined(requestTo(host, instanceBPath))).isTrue()
    }
}
