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
import java.io.IOException
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response
import org.hisp.dhis.android.core.arch.api.authentication.internal.UserIdAuthenticatorHelper.Companion.AUTHORIZATION_KEY
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore

@Reusable
internal class ParentAuthenticator @Inject constructor(
    private val credentialsSecureStore: CredentialsSecureStore,
    private val passwordAndCookieAuthenticator: PasswordAndCookieAuthenticator,
    private val openIDConnectAuthenticator: OpenIDConnectAuthenticator,
    private val cookieHelper: CookieAuthenticatorHelper
) :
    Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()

        // Header has already been explicitly added in UserService.authenticate
        val isLoginCall = req.header(AUTHORIZATION_KEY) != null

        return if (isLoginCall) {
            handleLoginCall(chain)
        } else {
            val credentials = credentialsSecureStore.get()
            return when {
                credentials?.password != null ->
                    passwordAndCookieAuthenticator.handlePasswordCall(chain, credentials)
                credentials?.openIDConnectState != null ->
                    openIDConnectAuthenticator.handleTokenCall(chain, credentials)
                else -> chain.proceed(req)
            }
        }
    }

    private fun handleLoginCall(chain: Interceptor.Chain): Response {
        cookieHelper.removeCookie()
        val res = chain.proceed(chain.request())
        cookieHelper.storeCookieIfSentByServer(res)
        return res
    }
}
