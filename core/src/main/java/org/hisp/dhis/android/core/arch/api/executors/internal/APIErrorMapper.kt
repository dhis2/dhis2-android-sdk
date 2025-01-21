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
package org.hisp.dhis.android.core.arch.api.executors.internal

import android.util.Log
import org.hisp.dhis.android.core.arch.api.internal.D2HttpException
import org.hisp.dhis.android.core.arch.api.internal.D2HttpResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.koin.core.annotation.Singleton
import java.io.EOFException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

@Singleton
@Suppress("TooManyFunctions")
internal class APIErrorMapper {

    fun mapHttpException(throwable: Throwable, errorBuilder: D2Error.Builder): D2Error {
        return when (throwable) {
            is SocketTimeoutException -> socketTimeoutException(errorBuilder, throwable)
            is UnknownHostException -> unknownHostException(errorBuilder, throwable)
            is ConnectException -> connectException(errorBuilder, throwable)
            is D2HttpException -> httpException(errorBuilder, throwable)
            is SSLException -> sslException(errorBuilder, throwable)
            is IOException -> ioException(errorBuilder, throwable)
            is Exception -> unexpectedException(errorBuilder, throwable)
            else -> unexpectedException(errorBuilder, RuntimeException(throwable))
        }
    }

    private fun logAndAppendOriginal(errorBuilder: D2Error.Builder, e: Exception): D2Error.Builder {
        Log.e(this.javaClass.simpleName, e.toString())
        return errorBuilder.originalException(e)
    }

    private fun socketTimeoutException(errorBuilder: D2Error.Builder, e: SocketTimeoutException): D2Error {
        return logAndAppendOriginal(errorBuilder, e)
            .errorCode(D2ErrorCode.SOCKET_TIMEOUT)
            .errorDescription("API call failed due to a SocketTimeoutException.")
            .build()
    }

    private fun unknownHostException(errorBuilder: D2Error.Builder, e: UnknownHostException): D2Error {
        return logAndAppendOriginal(errorBuilder, e)
            .errorCode(D2ErrorCode.UNKNOWN_HOST)
            .errorDescription("API call failed due to UnknownHostException")
            .build()
    }

    private fun connectException(errorBuilder: D2Error.Builder, e: ConnectException): D2Error {
        return logAndAppendOriginal(errorBuilder, e)
            .errorCode(D2ErrorCode.SERVER_CONNECTION_ERROR)
            .errorDescription("API call failed due to a ConnectException.")
            .build()
    }

    private fun sslException(errorBuilder: D2Error.Builder, sslException: SSLException): D2Error {
        return logAndAppendOriginal(errorBuilder, sslException)
            .errorDescription(sslException.message)
            .errorCode(D2ErrorCode.SSL_ERROR)
            .errorDescription("API call threw SSLException")
            .build()
    }

    private fun ioException(errorBuilder: D2Error.Builder, e: IOException): D2Error {
        val errorCode =
            if (e.cause is EOFException) {
                // This errorcode is used to consider the error as "offline".
                // The combination of ktor and the mockserver produces this exception when the mockserver is down.
                D2ErrorCode.SERVER_CONNECTION_ERROR
            } else {
                D2ErrorCode.API_RESPONSE_PROCESS_ERROR
            }

        return logAndAppendOriginal(errorBuilder, e)
            .errorCode(errorCode)
            .errorDescription("API call threw IOException")
            .build()
    }

    private fun httpException(errorBuilder: D2Error.Builder, e: D2HttpException): D2Error {
        return logAndAppendOriginal(errorBuilder, e)
            .url(e.response.requestUrl)
            .httpErrorCode(e.response.statusCode)
            .errorCode(D2ErrorCode.API_RESPONSE_PROCESS_ERROR)
            .errorDescription("API call threw HttpException")
            .build()
    }

    private fun unexpectedException(errorBuilder: D2Error.Builder, e: Exception): D2Error {
        return logAndAppendOriginal(errorBuilder, e)
            .errorCode(D2ErrorCode.UNEXPECTED)
            .errorDescription("Unexpected exception")
            .build()
    }

    fun getBaseErrorBuilder(response: D2HttpResponse): D2Error.Builder {
        return getBaseErrorBuilder()
            .url(response.requestUrl)
    }

    fun getBaseErrorBuilder(): D2Error.Builder {
        return D2Error.builder()
            .errorComponent(D2ErrorComponent.Server)
    }

    fun responseException(
        errorBuilder: D2Error.Builder,
        response: D2HttpResponse,
        errorCode: D2ErrorCode?,
    ): D2Error {
        val code = errorCode ?: D2ErrorCode.API_UNSUCCESSFUL_RESPONSE
        val serverMessage = response.errorBody.takeIf { it.isNotEmpty() } ?: getServerMessage(response)
        Log.e(this.javaClass.simpleName, serverMessage)
        return errorBuilder
            .errorCode(code)
            .httpErrorCode(response.statusCode)
            .errorDescription("API call failed, server message: $serverMessage")
            .build()
    }

    private fun getServerMessage(response: D2HttpResponse): String {
        val message =
            try {
                getIfNotEmpty(response.message)
                    ?: response.errorBody
            } catch (e: IOException) {
                null
            }

        return message ?: "No server message"
    }

    companion object {
        internal const val noErrorMessage: String = "No error message"
        internal fun getIfNotEmpty(message: String?): String? {
            return if (!message.isNullOrEmpty()) message else null
        }
    }
}
