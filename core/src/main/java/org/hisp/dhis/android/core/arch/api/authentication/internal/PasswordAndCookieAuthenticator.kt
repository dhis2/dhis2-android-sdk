/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.hisp.dhis.android.core.arch.api.internal.ServerURLWrapper
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.user.internal.ConnectLogoutHandler
import org.koin.core.annotation.Singleton

@Singleton
internal class PasswordAndCookieAuthenticator(
    private val userIdHelper: UserIdAuthenticatorHelper,
    private val cookieHelper: CookieAuthenticatorHelper,
    private val logoutHandler: ConnectLogoutHandler,
) {

    companion object {
        private val LOGIN_KEY_LIST = listOf("login.action", "dhis-web-login")
        const val LOCATION_KEY = "Location"
    }

    fun handlePasswordCall(chain: Interceptor.Chain, credentials: Credentials): Response {
        val builder = userIdHelper.builderWithUserId(chain)
        val useCookie = cookieHelper.isCookieDefined()
        val builderWithAuthentication =
            if (useCookie) cookieHelper.addCookieHeader(builder) else addPasswordHeader(builder, credentials)
        val res = chain.proceed(builderWithAuthentication.build())

        val isFromLoginCall = res.request.url.encodedPath.contains("auth/login")

        val finalRes = if (useCookie && hasAuthenticationFailed(res)) {
            res.close()
            cookieHelper.removeCookie()
            val newReqWithBasicAuth = addPasswordHeader(userIdHelper.builderWithUserId(chain), credentials).build()

            val res = chain.proceed(newReqWithBasicAuth)

            logoutOrReturnRes(res, isFromLoginCall)
        } else {
            logoutOrReturnRes(res, isFromLoginCall)
        }

        cookieHelper.storeCookieIfSentByServer(finalRes)
        return finalRes
    }

    private fun hasAuthenticationFailed(res: Response): Boolean {
        val location = res.header(LOCATION_KEY)
        return res.isRedirect && location != null && LOGIN_KEY_LIST.any { location.contains(it) }

    }

    private fun addPasswordHeader(builder: Request.Builder, credentials: Credentials): Request.Builder {
        return builder.addHeader(
            UserIdAuthenticatorHelper.AUTHORIZATION_KEY,
            UserIdAuthenticatorHelper.basic(credentials),
        )
    }

    private fun logoutOrReturnRes(res: Response, isFromLoginCall: Boolean) =
        if (res.code == 401 && !isFromLoginCall && ServerURLWrapper.serverUrl?.contains(res.request.url.host) == true) {
            logoutHandler.logOut()
            res
        } else {
            res
        }
}
