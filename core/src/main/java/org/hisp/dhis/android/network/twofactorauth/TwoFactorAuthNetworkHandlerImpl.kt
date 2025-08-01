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
package org.hisp.dhis.android.network.twofactorauth

import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.user.internal.TwoFactorAuthNetworkHandler
import org.hisp.dhis.android.network.common.dto.HttpMessageResponseDTO
import org.koin.core.annotation.Singleton

@Singleton
internal class TwoFactorAuthNetworkHandlerImpl(
    httpClient: HttpServiceClient,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
) : TwoFactorAuthNetworkHandler {
    private val service: TwoFactorAuthService = TwoFactorAuthService(httpClient)

    override suspend fun canTotp2faBeEnabled(): Boolean {
        val apiResponse = service.getTwoFactorMethods()
        return apiResponse.toDomain()
    }

    override suspend fun enrollTOTP2FA(): Result<HttpMessageResponse, D2Error> {
        return coroutineAPICallExecutor.wrap(
            storeError = false,
            acceptedErrorCodes = emptyList(),
            errorClassParser = HttpMessageResponseDTO::toErrorClass,
        ) {
            val apiResponse = service.enrollTOTP2FA()
            apiResponse.toDomain()
        }
    }

    override suspend fun getTotpSecret(): String {
        val qrCodeJsonDTO = service.getQrCodeJson()
        return qrCodeJsonDTO.toDomain()
    }

    override suspend fun is2faEnabled(): Boolean {
        return service.is2faEnabled()
    }

    override suspend fun enable2fa(code: Int): Result<HttpMessageResponse, D2Error> {
        return coroutineAPICallExecutor.wrap(
            storeError = false,
            acceptedErrorCodes = emptyList(),
            errorClassParser = HttpMessageResponseDTO::toErrorClass,
        ) {
            val apiResponse = service.enable2fa(code.toString())
            apiResponse.toDomain()
        }
    }

    override suspend fun disable2fa(code: Int): Result<HttpMessageResponse, D2Error> {
        return coroutineAPICallExecutor.wrap(
            storeError = false,
            acceptedErrorCodes = emptyList(),
            errorClassParser = HttpMessageResponseDTO::toErrorClass,
        ) {
            val apiResponse = service.disable2fa(code.toString())
            apiResponse.toDomain()
        }
    }
}
