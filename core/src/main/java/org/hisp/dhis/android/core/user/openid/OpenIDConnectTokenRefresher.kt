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

package org.hisp.dhis.android.core.user.openid

import android.content.Context
import io.reactivex.Single
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import org.koin.core.annotation.Singleton

@Singleton
internal class OpenIDConnectTokenRefresher(
    private val context: Context,
) {

    /**
     * Obtains a fresh idToken without side effects: it never closes the session, and it reports
     * whether a failure is worth retrying. On success [authState] has been updated in place, so the
     * caller is responsible for persisting it.
     */
    @Suppress("TooGenericExceptionCaught")
    fun refresh(authState: AuthState): OpenIdRefreshResult {
        val service = AuthorizationService(context)
        return try {
            val idToken = Single.create<String> { emitter ->
                authState.performActionWithFreshTokens(service) {
                        _: String?, freshIdToken: String?, ex: AuthorizationException? ->
                    service.dispose()
                    if (freshIdToken != null) {
                        emitter.onSuccess(freshIdToken)
                    } else {
                        emitter.onError(RefreshFailure(ex))
                    }
                }
            }.blockingGet()
            OpenIdRefreshResult.Success(idToken)
        } catch (e: Exception) {
            service.dispose()
            if (isRejectedByProvider(e)) OpenIdRefreshResult.Invalid else OpenIdRefreshResult.Retryable
        }
    }

    /**
     * Only a refresh token the provider explicitly rejects is unrecoverable. A token error is the
     * provider answering the request and refusing it; anything else — offline, timeout, an outage —
     * is transient and must leave the stored state alone so a later call can retry.
     */
    private fun isRejectedByProvider(error: Throwable?): Boolean {
        var current = error
        while (current != null) {
            if (current is RefreshFailure) {
                return current.authorizationException?.type == AuthorizationException.TYPE_OAUTH_TOKEN_ERROR
            }
            current = current.cause
        }
        return false
    }

    /** Carries the AppAuth failure through RxJava so it can be classified. */
    private class RefreshFailure(
        val authorizationException: AuthorizationException?,
    ) : RuntimeException(authorizationException)
}
