/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.arch.api.executors.internal;

import android.util.Log;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.inject.Inject;
import javax.net.ssl.SSLException;

import dagger.Reusable;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

@Reusable
final class APIErrorMapper {

    @Inject
    APIErrorMapper() {
        // Constructor only to enable injection
    }

    D2Error mapRetrofitException(Throwable throwable, D2Error.Builder errorBuilder) {
        if (throwable instanceof SocketTimeoutException) {
            return socketTimeoutException(errorBuilder, (SocketTimeoutException) throwable);
        } else if (throwable instanceof UnknownHostException) {
            return unknownHostException(errorBuilder, (UnknownHostException) throwable);
        } else if (throwable instanceof HttpException) {
            return httpException(errorBuilder, (HttpException) throwable);
        } else if (throwable instanceof SSLException) {
            return sslException(errorBuilder, (SSLException) throwable);
        } else if (throwable instanceof IOException) {
            return ioException(errorBuilder, (IOException) throwable);
        } else if (throwable instanceof Exception) {
            return unexpectedException(errorBuilder, (Exception) throwable);
        } else {
            return unexpectedException(errorBuilder, new RuntimeException(throwable));
        }
    }

    private D2Error.Builder logAndAppendOriginal(D2Error.Builder errorBuilder, Exception e) {
        Log.e(this.getClass().getSimpleName(), e.toString());
        return errorBuilder.originalException(e);
    }

    private D2Error socketTimeoutException(D2Error.Builder errorBuilder, SocketTimeoutException e) {
        return logAndAppendOriginal(errorBuilder, e)
                .errorCode(D2ErrorCode.SOCKET_TIMEOUT)
                .errorDescription("API call failed due to a SocketTimeoutException.")
                .build();
    }

    private D2Error unknownHostException(D2Error.Builder errorBuilder, UnknownHostException e) {
        return logAndAppendOriginal(errorBuilder, e)
                .errorCode(D2ErrorCode.UNKNOWN_HOST)
                .errorDescription("API call failed due to UnknownHostException")
                .build();
    }

    private D2Error sslException(D2Error.Builder errorBuilder, SSLException sslException) {
        return logAndAppendOriginal(errorBuilder, sslException)
                .errorDescription(sslException.getMessage())
                .errorCode(D2ErrorCode.SSL_ERROR)
                .errorDescription("API call threw SSLException")
                .build();
    }

    private D2Error ioException(D2Error.Builder errorBuilder, IOException e) {
        return logAndAppendOriginal(errorBuilder, e)
                .errorCode(D2ErrorCode.API_RESPONSE_PROCESS_ERROR)
                .errorDescription("API call threw IOException")
                .build();
    }

    private D2Error httpException(D2Error.Builder errorBuilder, HttpException e) {
        return logAndAppendOriginal(errorBuilder, e)
                .url(getUrl(e.response().raw().request()))
                .httpErrorCode(e.response().code())
                .errorCode(D2ErrorCode.API_RESPONSE_PROCESS_ERROR)
                .errorDescription("API call threw HttpException")
                .build();
    }

    private D2Error unexpectedException(D2Error.Builder errorBuilder, Exception e) {
        return logAndAppendOriginal(errorBuilder, e)
                .errorCode(D2ErrorCode.UNEXPECTED)
                .errorDescription("Unexpected exception")
                .build();
    }

    D2Error.Builder getCollectionErrorBuilder(Call<?> call) {
        return getBaseErrorBuilder(call);
    }

    D2Error.Builder getObjectErrorBuilder(Call<?> call) {
        return getBaseErrorBuilder(call);
    }

    D2Error.Builder getRxObjectErrorBuilder() {
        return D2Error.builder()
                .errorComponent(D2ErrorComponent.Server);
    }

    private D2Error.Builder getBaseErrorBuilder(Call<?> call) {
        return D2Error.builder()
                .url(getUrl(call))
                .errorComponent(D2ErrorComponent.Server);
    }

    private String getUrl(Call<?> call) {
        return getUrl(call.request());
    }

    private String getUrl(Request request) {
        if (request == null || request.url() == null) {
            return null;
        } else {
            return request.url().toString();
        }
    }

    D2Error responseException(D2Error.Builder errorBuilder, Response<?> response) {
        return responseException(errorBuilder, response, D2ErrorCode.API_UNSUCCESSFUL_RESPONSE);
    }

    D2Error responseException(D2Error.Builder errorBuilder, Response<?> response, D2ErrorCode errorCode) {
        String serverMessage = getServerMessage(response);
        Log.e(this.getClass().getSimpleName(), serverMessage);
        return errorBuilder
                .errorCode(errorCode)
                .httpErrorCode(response.code())
                .errorDescription("API call failed, server message: " + serverMessage)
                .build();
    }

    private boolean nonEmptyMessage(String message) {
        return message != null && message.length() > 0;
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private String getServerMessage(Response<?> response) {
        if (nonEmptyMessage(response.message())) {
            return response.message();
        }

        try {
            String errorBodyString = response.errorBody().string();
            if (nonEmptyMessage(errorBodyString)) {
                return errorBodyString;
            }
            if (nonEmptyMessage(response.errorBody().toString())) {
                return response.errorBody().toString();
            }
        } catch (IOException e) {
            // IGNORE
        }
        return "No server message";
    }
}