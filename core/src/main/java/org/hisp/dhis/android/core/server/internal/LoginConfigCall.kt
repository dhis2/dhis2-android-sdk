/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.core.server.internal

import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.server.LoginConfig
import org.hisp.dhis.android.core.systeminfo.internal.PingNetworkHandler
import org.hisp.dhis.android.core.user.internal.LogInExceptions
import org.koin.core.annotation.Singleton

@Singleton
internal class LoginConfigCall(
    private val pingNetworkHandler: PingNetworkHandler,
    private val loginExceptions: LogInExceptions,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val networkHandler: LoginConfigNetworkHandler,
) {
    suspend fun checkServerUrl(serverUrl: String): Result<LoginConfig, D2Error> {
        val loginConfig = tryLoginConfig(serverUrl)

        return when (loginConfig) {
            is Result.Success<LoginConfig, D2Error> -> loginConfig
            is Result.Failure<LoginConfig, D2Error> -> tryPing(serverUrl)
        }
    }

    private suspend fun tryLoginConfig(serverUrl: String): Result<LoginConfig, D2Error> {
        return coroutineAPICallExecutor.wrap(storeError = false) {
            networkHandler.loginConfigFor(serverUrl)
        }
    }

    private suspend fun tryPing(serverUrl: String): Result<LoginConfig, D2Error> {
        val pingResult = coroutineAPICallExecutor.wrap(storeError = false) {
            pingNetworkHandler.getPingFor(serverUrl)
        }

        return pingResult
            .map {
                // If the ping request is successful, it might be caused by the actual ping or a successful redirection
                // A redirection doesn't mean it is a valid DHIS2 instance, but it is not possible to tell the opposite
                LoginConfig.createDefault(serverUrl = serverUrl)
            }
            .mapFailure { d2Error ->
                if (d2Error.errorCode() == D2ErrorCode.UNEXPECTED ||
                    d2Error.errorCode() == D2ErrorCode.API_RESPONSE_PROCESS_ERROR ||
                    d2Error.errorCode() == D2ErrorCode.API_UNSUCCESSFUL_RESPONSE
                ) {
                    loginExceptions.noDHIS2Server()
                } else {
                    d2Error
                }
            }
    }
}
