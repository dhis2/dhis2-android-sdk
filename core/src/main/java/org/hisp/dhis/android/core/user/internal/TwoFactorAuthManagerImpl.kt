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

package org.hisp.dhis.android.core.user.internal

import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.user.TwoFactorAuthManager
import org.hisp.dhis.android.network.twofactorauth.TwoFactorAuthNetworkHandlerImpl
import org.koin.core.annotation.Singleton

@Singleton
internal class TwoFactorAuthManagerImpl(
    private val twoFactorAuthNetworkHandler: TwoFactorAuthNetworkHandlerImpl,
) : TwoFactorAuthManager {
    override suspend fun canTotp2faBeEnabled(): Result<Boolean, D2Error> {
        return twoFactorAuthNetworkHandler.canTotp2faBeEnabled()
    }

    override suspend fun is2faEnabled(): Boolean {
        return twoFactorAuthNetworkHandler.is2faEnabled().getOrNull() ?: false
    }

    override suspend fun getTotpSecret(): String {
        return try {
            twoFactorAuthNetworkHandler.getTotpSecret().getOrThrow()
        } catch (error: D2Error) {
            if (error.errorCode() == D2ErrorCode.NOT_IN_TOTP_2FA_ENROLLMENT_MODE) {
                twoFactorAuthNetworkHandler.enrollTOTP2FA().getOrThrow()
                twoFactorAuthNetworkHandler.getTotpSecret().getOrThrow()
            } else {
                throw error
            }
        }
    }

    override suspend fun enable2fa(code: String): Result<HttpMessageResponse, D2Error> {
        return twoFactorAuthNetworkHandler.enable2fa(code)
    }

    override suspend fun disable2fa(code: String): Result<HttpMessageResponse, D2Error> {
        return twoFactorAuthNetworkHandler.disable2fa(code)
    }
}
