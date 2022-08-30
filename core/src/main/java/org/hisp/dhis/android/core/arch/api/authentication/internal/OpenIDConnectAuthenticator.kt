/*
 *  Copyright (c) 2004-2022, University of Oslo
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

import dagger.Reusable
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.hisp.dhis.android.core.arch.api.authentication.internal.UserIdAuthenticatorHelper.Companion.AUTHORIZATION_KEY
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.user.openid.OpenIDConnectLogoutHandler
import org.hisp.dhis.android.core.user.openid.OpenIDConnectTokenRefresher

private const val UNAUTHORIZED = 401

@Reusable
internal class OpenIDConnectAuthenticator @Inject constructor(
    private val credentialsSecureStore: CredentialsSecureStore,
    private val tokenRefresher: OpenIDConnectTokenRefresher,
    private val userIdHelper: UserIdAuthenticatorHelper,
    private val logoutHandler: OpenIDConnectLogoutHandler
) {

    fun handleTokenCall(chain: Interceptor.Chain, credentials: Credentials): Response {
        val builder = userIdHelper.builderWithUserId(chain)
        val builderWithAuthentication = addTokenHeader(builder, getUpdatedToken(credentials))
        val res = chain.proceed(builderWithAuthentication.build())
        if (res.code() == UNAUTHORIZED) {
            logoutHandler.logOut()
        }
        return res
    }

    private fun getUpdatedToken(credentials: Credentials): String {
        val state = credentials.openIDConnectState!!
        return if (state.needsTokenRefresh) {
            val token = tokenRefresher.blockingGetFreshToken(state)
            credentialsSecureStore.set(credentials) // Auth state internally updated
            token
        } else {
            state.idToken!!
        }
    }

    private fun addTokenHeader(builder: Request.Builder, token: String): Request.Builder {
        return builder.addHeader(AUTHORIZATION_KEY, "Bearer $token")
    }
}
