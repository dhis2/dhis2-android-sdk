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

import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.api.Send.Sender
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.takeFrom
import io.ktor.client.statement.HttpResponse
import org.hisp.dhis.android.core.arch.api.internal.HttpStatusCodes.REDIRECT_MAX
import org.hisp.dhis.android.core.arch.api.internal.HttpStatusCodes.REDIRECT_MIN
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.koin.core.annotation.Singleton

@Singleton
internal class PasswordAndCookieAuthenticatorPlugin(
    private val userIdHelper: UserIdAuthenticatorHelperPlugin,
    private val cookieHelper: CookieAuthenticatorHelperPlugin,
) {

    companion object {
        private const val LOGIN_ACTION = "login.action"
        const val LOCATION_KEY = "Location"
    }

    suspend fun handlePasswordCall(
        sender: Sender,
        requestBuilder: HttpRequestBuilder,
        credentials: Credentials,
    ): HttpClientCall {
        userIdHelper.builderWithUserId(requestBuilder)
        val useCookie = cookieHelper.isCookieDefined()
        if (useCookie) {
            cookieHelper.addCookieHeader(requestBuilder)
        } else {
            addPasswordHeader(requestBuilder, credentials)
        }
        val call = sender.proceed(requestBuilder)

        val finalCall = if (useCookie && hasAuthenticationFailed(call.response)) {
            cookieHelper.removeCookie()
            val originalRequest: HttpRequestBuilder = HttpRequestBuilder().apply {
                takeFrom(call.request)
            }

            userIdHelper.builderWithUserId(originalRequest)
            addPasswordHeader(originalRequest, credentials)
            sender.proceed(originalRequest)
        } else {
            call
        }

        cookieHelper.storeCookieIfSentByServer(finalCall.response)
        return finalCall
    }

    private fun hasAuthenticationFailed(res: HttpResponse): Boolean {
        val location = res.headers[LOCATION_KEY]
        return res.status.value in REDIRECT_MIN..REDIRECT_MAX &&
            location != null &&
            location.contains(LOGIN_ACTION)
    }

    private fun addPasswordHeader(requestBuilder: HttpRequestBuilder, credentials: Credentials) {
        requestBuilder.apply {
            headers.remove(UserIdAuthenticatorHelperPlugin.AUTHORIZATION_KEY)
            header(
                UserIdAuthenticatorHelperPlugin.AUTHORIZATION_KEY,
                UserIdAuthenticatorHelperPlugin.basic(credentials),
            )
        }
    }
}
