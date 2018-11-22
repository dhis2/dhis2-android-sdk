/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.common;

import android.util.Log;

import org.hisp.dhis.android.core.ObjectMapperFactory;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public final class APICallExecutor {

    public <P> List<P> executePayloadCall(Call<Payload<P>> call) throws D2Error {
        D2Error.Builder errorBuilder = getCollectionErrorBuilder(call);

        try {
            Response<Payload<P>> response = call.execute();
            if (response.isSuccessful()) {
                if (response.body() == null) {
                    throw responseException(errorBuilder, response);
                } else {
                    return response.body().items();
                }
            } else {
                throw responseException(errorBuilder, response);
            }
        } catch (SocketTimeoutException e) {
            throw socketTimeoutException(errorBuilder, e);
        } catch (IOException e) {
            throw ioException(errorBuilder, e);
        }
    }

    public <P> P executeObjectCall(Call<P> call) throws D2Error {
        return executeObjectCallInternal(call, new ArrayList<Integer>(), null, null);
    }

    public <P> P executeObjectCallWithAcceptedErrorCodes(Call<P> call, List<Integer> acceptedErrorCodes,
                                                         Class<P> errorClass) throws D2Error {
        return executeObjectCallInternal(call, acceptedErrorCodes, errorClass, null);
    }

    public <P> P executeObjectCallWithErrorCatcher(Call<P> call, APICallErrorCatcher errorCatcher)
            throws D2Error {
        return executeObjectCallInternal(call, new ArrayList<Integer>(), null, errorCatcher);
    }

    private <P> P executeObjectCallInternal(Call<P> call,
                                            List<Integer> acceptedErrorCodes,
                                            Class<P> errorClass,
                                            APICallErrorCatcher errorCatcher) throws D2Error {

        D2Error.Builder errorBuilder = getObjectErrorBuilder(call);

        try {
            Response<P> response = call.execute();
            if (response.isSuccessful()) {
                return processSuccessfulResponse(errorBuilder, response);
            } else if (errorClass != null && acceptedErrorCodes.contains(response.code())) {
                return ObjectMapperFactory.objectMapper().readValue(response.errorBody().string(), errorClass);
            } else if (errorCatcher == null) {
                throw responseException(errorBuilder, response);
            } else {
                D2ErrorCode d2ErrorCode = errorCatcher.catchError(response);
                if (d2ErrorCode == null) {
                    throw responseException(errorBuilder, response);
                } else {
                    throw errorBuilder.errorCode(d2ErrorCode).build();
                }
            }
        } catch (SocketTimeoutException e) {
            throw socketTimeoutException(errorBuilder, e);
        } catch (UnknownHostException e) {
            throw unknownHostException(errorBuilder, e);
        } catch (IOException e) {
            throw ioException(errorBuilder, e);
        }
    }

    private <P> P processSuccessfulResponse(D2Error.Builder errorBuilder, Response<P> response) throws D2Error {
        if (response.body() == null) {
            throw responseException(errorBuilder, response);
        } else {
            return response.body();
        }
    }

    private D2Error.Builder getCollectionErrorBuilder(Call<?> call) {
        return D2Error.builder()
                .resourceType("TODO") // TODO
                .uid(null)
                .url(getUrl(call))
                .errorComponent(D2ErrorComponent.Server);
    }

    private D2Error.Builder getObjectErrorBuilder(Call<?> call) {
        return D2Error.builder()
                .resourceType("TODO") // TODO
                .uid("TODO") // TODO
                .url(getUrl(call))
                .errorComponent(D2ErrorComponent.Server);
    }

    private String getUrl(Call<?> call) {
        if (call.request() == null || call.request().url() == null) {
            return null;
        } else {
            return call.request().url().toString();
        }
    }

    private D2Error responseException(D2Error.Builder errorBuilder, Response<?> response) {
        String serverMessage = getServerMessage(response);
        Log.e(this.getClass().getSimpleName(), serverMessage);
        return errorBuilder
                .errorCode(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE)
                .httpErrorCode(response.code())
                .errorDescription("API call failed, response: " + serverMessage)
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

    private D2Error socketTimeoutException(D2Error.Builder errorBuilder, SocketTimeoutException e) {
        Log.e(this.getClass().getSimpleName(), e.toString());
        return errorBuilder
                .errorCode(D2ErrorCode.SOCKET_TIMEOUT)
                .errorDescription("API call failed due to a SocketTimeoutException.")
                .originalException(e)
                .build();
    }

    private D2Error unknownHostException(D2Error.Builder errorBuilder, UnknownHostException e) {
        Log.e(this.getClass().getSimpleName(), e.toString());
        return errorBuilder
                .errorCode(D2ErrorCode.UNKNOWN_HOST)
                .errorDescription("API call failed due to UnknownHostException")
                .originalException(e)
                .build();
    }

    private D2Error ioException(D2Error.Builder errorBuilder, IOException e) {
        Log.e(this.getClass().getSimpleName(), e.toString());
        return errorBuilder
                .errorCode(D2ErrorCode.API_RESPONSE_PROCESS_ERROR)
                .errorDescription("API call threw IOException")
                .originalException(e)
                .build();
    }
}