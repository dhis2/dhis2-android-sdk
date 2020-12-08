/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.arch.api.authentication.internal

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.hisp.dhis.android.core.arch.helpers.UserHelper
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore
import java.io.IOException
import java.util.Locale

internal class BasicAuthenticator(private val credentialsSecureStore: ObjectKeyValueStore<Credentials>) :
    Authenticator {

    companion object {
        private const val AUTHORIZATION_KEY = "Authorization"
        private const val COOKIE_KEY = "Cookie"
        private const val SET_COOKIE_KEY = "set-cookie"
        private const val LOCATION_KEY = "Location"

        private const val BASIC_CREDENTIALS = "Basic %s"
    }

    private var cookieValue: String? = null

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()

        // Header has already been explicitly in UserService.authenticate
        val isLoginCall = req.header(AUTHORIZATION_KEY) != null

        return if (isLoginCall) {
            handleLoginCall(chain)
        } else {
            val credentials = credentialsSecureStore.get()
            return if (credentials == null) {
                chain.proceed(req)
            } else {
                addAuthenticationHeaderOrCookie(chain, credentials)
            }
        }
    }

    private fun handleLoginCall(chain: Interceptor.Chain): Response {
        removeCookie()
        val res = chain.proceed(chain.request())
        storeCookieIfSentByServer(res)
        return res
    }

    private fun storeCookieIfSentByServer(res: Response) {
        val cookieRes = res.header(SET_COOKIE_KEY)
        if (cookieRes != null) {
            cookieValue = cookieRes
        }
    }

    private fun addAuthenticationHeaderOrCookie(chain: Interceptor.Chain, credentials: Credentials): Response {
        val req = chain.request()
        val builder = req.newBuilder()
        val useCookie = cookieValue != null
        val builderWithAuthentication =
            if (useCookie) addCookieHeader(builder) else addAuthorizationHeader(builder, credentials)
        val res = chain.proceed(builderWithAuthentication.build())

        val finalRes = if (useCookie && hasAuthenticationFailed(res)) {
            removeCookie()
            val newReqWithBasicAuth = addAuthorizationHeader(req.newBuilder(), credentials).build()
            return chain.proceed(newReqWithBasicAuth)
        } else {
            res
        }

        storeCookieIfSentByServer(finalRes)
        return finalRes
    }

    private fun hasAuthenticationFailed(res: Response): Boolean {
        val location = res.header(LOCATION_KEY)
        return res.isRedirect && location != null && location.contains("login.action")
    }

    private fun addAuthorizationHeader(builder: Request.Builder, credentials: Credentials): Request.Builder {
        val base64Credentials = UserHelper.base64(credentials.username(), credentials.password())
        return builder.addHeader(
            AUTHORIZATION_KEY, String.format(
                Locale.US,
                BASIC_CREDENTIALS, base64Credentials
            )
        )
    }

    private fun addCookieHeader(builder: Request.Builder): Request.Builder {
        return builder.addHeader(COOKIE_KEY, cookieValue!!)
    }

    private fun removeCookie() {
        cookieValue = null
    }
}