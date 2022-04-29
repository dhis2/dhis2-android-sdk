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

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Reusable
import java.lang.Exception
import java.net.HttpURLConnection
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallErrorCatcher
import org.hisp.dhis.android.core.arch.api.executors.internal.APIErrorMapper
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import retrofit2.Response

@Reusable
internal class UserAuthenticateCallErrorCatcher @Inject constructor(private val objectMapper: ObjectMapper) :
    APICallErrorCatcher {
    override fun mustBeStored(): Boolean {
        return true
    }

    @Suppress("TooGenericExceptionCaught")
    override fun catchError(response: Response<*>, errorBody: String): D2ErrorCode {
        return try {
            if (errorBody == APIErrorMapper.noErrorMessage) {
                D2ErrorCode.NO_DHIS2_SERVER
            } else {
                val errorResponse = objectMapper.readValue(errorBody, HttpMessageResponse::class.java)
                val isUnauthorized = response.code() == HttpURLConnection.HTTP_UNAUTHORIZED
                if (isUnauthorized && errorResponse.message().contains("Account locked")) {
                    D2ErrorCode.USER_ACCOUNT_LOCKED
                } else if (isUnauthorized) {
                    D2ErrorCode.BAD_CREDENTIALS
                } else if (hasInvalidCharacters(response.code(), errorBody)) {
                    D2ErrorCode.INVALID_CHARACTERS
                } else {
                    D2ErrorCode.NO_DHIS2_SERVER
                }
            }
        } catch (e: Exception) {
            if (hasInvalidCharacters(response.code(), errorBody)) {
                D2ErrorCode.INVALID_CHARACTERS
            } else {
                Log.e(UserAuthenticateCallErrorCatcher::class.java.simpleName, e.javaClass.simpleName, e)
                D2ErrorCode.NO_DHIS2_SERVER
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun hasInvalidCharacters(code: Int, errorBodyStr: String): Boolean {
        return try {
            val isBadRequest = code == HttpURLConnection.HTTP_BAD_REQUEST
            isBadRequest && errorBodyStr.contains("Invalid character")
        } catch (e: Exception) {
            false
        }
    }
}
