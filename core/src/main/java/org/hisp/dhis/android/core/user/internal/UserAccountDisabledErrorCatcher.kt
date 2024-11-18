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

import com.fasterxml.jackson.databind.ObjectMapper
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallErrorCatcher
import org.hisp.dhis.android.core.arch.api.internal.D2HttpResponse
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.user.AccountDeletionReason
import org.koin.core.annotation.Singleton
import java.net.HttpURLConnection

@Singleton
@Suppress("TooGenericExceptionCaught")
internal class UserAccountDisabledErrorCatcher(
    private val objectMapper: ObjectMapper,
    private val accountManager: AccountManagerImpl,
) : APICallErrorCatcher {

    override fun mustBeStored(): Boolean {
        return true
    }

    override fun catchError(response: D2HttpResponse): D2ErrorCode? {
        return try {
            accountManager.deleteCurrentAccountAndEmit(AccountDeletionReason.ACCOUNT_DISABLED)
            D2ErrorCode.USER_ACCOUNT_DISABLED
        } catch (e: Throwable) {
            D2ErrorCode.USER_ACCOUNT_DISABLED
        }
    }

    fun isUserAccountLocked(response: D2HttpResponse): Boolean {
        return try {
            val isUnauthorized = response.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED
            val responseErrorBody = objectMapper.readValue(response.errorBody, HttpMessageResponse::class.java)
            isUnauthorized && responseErrorBody.message().contains("Account disabled")
        } catch (e: Exception) {
            false
        }
    }
}
