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
package org.hisp.dhis.android.core.user.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent

@Reusable
internal class LogInExceptions @Inject internal constructor(
    private val credentialsSecureStore: CredentialsSecureStore
) {

    @Throws(D2Error::class)
    fun throwExceptionIfUsernameNull(username: String?) {
        if (username == null) {
            throw D2Error.builder()
                .errorCode(D2ErrorCode.LOGIN_USERNAME_NULL)
                .errorDescription("Username is null")
                .errorComponent(D2ErrorComponent.SDK)
                .build()
        }
    }

    @Throws(D2Error::class)
    fun throwExceptionIfPasswordNull(password: String?) {
        if (password == null) {
            throw D2Error.builder()
                .errorCode(D2ErrorCode.LOGIN_PASSWORD_NULL)
                .errorDescription("Password is null")
                .errorComponent(D2ErrorComponent.SDK)
                .build()
        }
    }

    @Throws(D2Error::class)
    fun throwExceptionIfAlreadyAuthenticated() {
        val credentials = credentialsSecureStore.get()
        if (credentials != null) {
            throw D2Error.builder()
                .errorCode(D2ErrorCode.ALREADY_AUTHENTICATED)
                .errorDescription("A user is already authenticated: " + credentials.username)
                .errorComponent(D2ErrorComponent.SDK)
                .build()
        }
    }

    fun noDHIS2Server(): D2Error {
        return D2Error.builder()
            .errorCode(D2ErrorCode.NO_DHIS2_SERVER)
            .errorDescription("The URL is no DHIS2 server")
            .errorComponent(D2ErrorComponent.SDK)
            .build()
    }

    fun noUserOfflineError(): D2Error {
        return D2Error.builder()
            .errorCode(D2ErrorCode.NO_AUTHENTICATED_USER_OFFLINE)
            .errorDescription("The user hasn't been previously authenticated. Cannot login offline.")
            .errorComponent(D2ErrorComponent.SDK)
            .build()
    }

    fun badCredentialsError(): D2Error {
        throw D2Error.builder()
            .errorCode(D2ErrorCode.BAD_CREDENTIALS)
            .errorDescription("Credentials do not match authenticated user. Cannot login offline.")
            .errorComponent(D2ErrorComponent.SDK)
            .build()
    }
}
