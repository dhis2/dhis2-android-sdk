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
package org.hisp.dhis.android.core.arch.api.executors.internal

import android.util.Log
import dagger.Reusable
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.net.ssl.SSLException
import okhttp3.Request
import org.hisp.dhis.android.core.arch.api.internal.DynamicServerURLInterceptor
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response

@Reusable
@Suppress("TooManyFunctions")
internal class APIErrorMapper @Inject constructor() {

    fun mapRetrofitException(throwable: Throwable, errorBuilder: D2Error.Builder): D2Error {
        return when (throwable) {
            is SocketTimeoutException -> socketTimeoutException(errorBuilder, throwable)
            is UnknownHostException -> unknownHostException(errorBuilder, throwable)
            is HttpException -> httpException(errorBuilder, throwable)
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

    private fun sslException(errorBuilder: D2Error.Builder, sslException: SSLException): D2Error {
        return logAndAppendOriginal(errorBuilder, sslException)
            .errorDescription(sslException.message)
            .errorCode(D2ErrorCode.SSL_ERROR)
            .errorDescription("API call threw SSLException")
            .build()
    }

    private fun ioException(errorBuilder: D2Error.Builder, e: IOException): D2Error {
        return logAndAppendOriginal(errorBuilder, e)
            .errorCode(D2ErrorCode.API_RESPONSE_PROCESS_ERROR)
            .errorDescription("API call threw IOException")
            .build()
    }

    private fun httpException(errorBuilder: D2Error.Builder, e: HttpException): D2Error {
        return logAndAppendOriginal(errorBuilder, e)
            .url(e.response()?.raw()?.request()?.url()?.toString())
            .httpErrorCode(e.response()!!.code())
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

    val rxObjectErrorBuilder: D2Error.Builder
        get() = D2Error.builder()
            .errorComponent(D2ErrorComponent.Server)

    fun getBaseErrorBuilder(call: Call<*>): D2Error.Builder {
        return D2Error.builder()
            .url(getUrl(call.request()))
            .errorComponent(D2ErrorComponent.Server)
    }

    private fun getUrl(request: Request?): String? {
        return request?.url()?.toString()?.let {
            DynamicServerURLInterceptor.transformUrl(it)
        }
    }

    @JvmOverloads
    fun responseException(
        errorBuilder: D2Error.Builder,
        response: Response<*>,
        errorCode: D2ErrorCode? = D2ErrorCode.API_UNSUCCESSFUL_RESPONSE,
        errorBody: String?
    ): D2Error {
        val serverMessage = errorBody ?: getServerMessage(response)
        Log.e(this.javaClass.simpleName, serverMessage)
        return errorBuilder
            .errorCode(errorCode)
            .httpErrorCode(response.code())
            .errorDescription("API call failed, server message: $serverMessage")
            .build()
    }

    private fun getIfNotEmpty(message: String?): String? {
        return if (message != null && message.isNotEmpty()) message else null
    }

    private fun getServerMessage(response: Response<*>): String {
        val message =
            try {
                getIfNotEmpty(response.message())
                    ?: getIfNotEmpty(response.errorBody()!!.string())
                    ?: getIfNotEmpty(response.errorBody().toString())
            } catch (e: IOException) {
                null
            }

        return message ?: "No server message"
    }

    fun getErrorBody(response: Response<*>): String {
        val errorBody =
            try {
                getIfNotEmpty(response.errorBody()!!.string()) ?: getIfNotEmpty(response.errorBody().toString())
            } catch (e: IOException) {
                null
            }

        return errorBody ?: noErrorMessage
    }

    companion object {
        internal const val noErrorMessage: String = "No error message"
    }
}
