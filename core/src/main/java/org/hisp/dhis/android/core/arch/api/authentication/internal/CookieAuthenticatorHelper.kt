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

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import org.koin.core.annotation.Singleton

@Singleton
internal class CookieAuthenticatorHelper {

    companion object {
        private const val COOKIE_KEY = "Cookie"
        private const val SET_COOKIE_KEY = "set-cookie"
        private const val PATH_ATTRIBUTE = "path"
        private const val DEFAULT_PATH = "/"
    }

    private data class StoredCookie(val nameValue: String, val path: String)

    private val cookieMapByHost = mutableMapOf<String, MutableMap<String, StoredCookie>>()

    fun storeCookieIfSentByServer(res: HttpResponse) {
        val host = res.call.request.url.host
        val cookies = res.headers.getAll(SET_COOKIE_KEY)

        if (!cookies.isNullOrEmpty()) {
            val hostCookies = cookieMapByHost.getOrPut(host) { mutableMapOf() }
            cookies.forEach { cookie ->
                val nameValue = cookie.substringBefore(";")
                val name = nameValue.substringBefore("=").trim()
                if (name.isNotEmpty()) {
                    val path = extractPath(cookie)
                    hostCookies[cookieId(name, path)] = StoredCookie(nameValue, path)
                }
            }
        }
    }

    fun isCookieDefined(requestBuilder: HttpRequestBuilder): Boolean {
        return matchingCookies(requestBuilder).isNotEmpty()
    }

    fun removeCookie(requestBuilder: HttpRequestBuilder) {
        val host = requestBuilder.url.host
        val requestPath = requestBuilder.url.build().encodedPath
        cookieMapByHost[host]?.let { hostCookies ->
            hostCookies.values.removeAll { pathMatches(it.path, requestPath) }
            if (hostCookies.isEmpty()) {
                cookieMapByHost.remove(host)
            }
        }
    }

    fun addCookieHeader(requestBuilder: HttpRequestBuilder) {
        val matching = matchingCookies(requestBuilder)
        if (matching.isNotEmpty()) {
            requestBuilder.apply {
                headers.remove(COOKIE_KEY)
                header(COOKIE_KEY, matching.joinToString("; ") { it.nameValue })
            }
        }
    }

    /**
     * Cookies are stored per host, but a host can serve several DHIS2 instances under different
     * paths (e.g. `.../stable-2-41-9` and `.../stable-2-43-0-1`). Each cookie carries its own
     * `Path`, so a cookie is only attached to a request whose path matches that `Path`, avoiding
     * that an instance receives another instance's cookie.
     */
    private fun matchingCookies(requestBuilder: HttpRequestBuilder): List<StoredCookie> {
        val requestPath = requestBuilder.url.build().encodedPath
        return cookieMapByHost[requestBuilder.url.host]
            ?.values
            ?.filter { pathMatches(it.path, requestPath) }
            .orEmpty()
    }

    private fun extractPath(cookie: String): String {
        return cookie.split(";")
            .map { it.trim() }
            .firstOrNull { it.substringBefore("=").trim().equals(PATH_ATTRIBUTE, ignoreCase = true) }
            ?.substringAfter("=")
            ?.trim()
            ?.ifEmpty { DEFAULT_PATH }
            ?: DEFAULT_PATH
    }

    private fun pathMatches(cookiePath: String, requestPath: String): Boolean {
        return cookiePath == requestPath ||
            (
                requestPath.startsWith(cookiePath) &&
                    (cookiePath.endsWith("/") || requestPath.getOrNull(cookiePath.length) == '/')
                )
    }

    private fun cookieId(name: String, path: String): String = "$name@$path"
}
