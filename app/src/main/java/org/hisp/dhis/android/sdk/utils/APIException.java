/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.utils;

import org.hisp.dhis.android.sdk.network.http.Response;

import java.io.IOException;

public class APIException extends Exception {
    private final String mUrl;
    private final Response mResponse;
    private final boolean mNetworkError;
    private final boolean mHttpError;
    private final boolean mConversionError;


    private APIException(String url, String message, Response response, Throwable exception,
                         boolean networkError, boolean httpError, boolean conversionError) {
        super(message, exception);
        mUrl = url;
        mResponse = response;
        mNetworkError = networkError;
        mHttpError = httpError;
        mConversionError = conversionError;
    }

    public static APIException networkError(String url, IOException exception) {
        return new APIException(url, exception.getMessage(), null, exception,
                true, false, false);
    }

    public static APIException httpError(String url, Response response) {
        String message = response.getStatus() + " " + response.getReason();
        return new APIException(url, message, response, null, false, true, false);
    }

    public static APIException conversionError(String url, Response response,
                                               Exception exception) {
        return new APIException(url, exception.getMessage(), response,
                exception, false, false, true);
    }

    public static APIException unexpectedError(String url, Throwable exception) {
        return new APIException(url, exception.getMessage(), null, exception, false, false, false);
    }

    /**
     * The request URL which produced the error.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Response object containing status code, headers, body, etc.
     */
    public Response getResponse() {
        return mResponse;
    }

    /**
     * Whether or not this error was the result of a network error.
     */
    public boolean isNetworkError() {
        return mNetworkError;
    }

    /**
     * Whether or not this error was the result of a http error.
     */
    public boolean isHttpError() {
        return mHttpError;
    }

    /**
     * Whether or not this error was the result of a conversion error.
     */
    public boolean isConversionError() {
        return mConversionError;
    }

    /**
     * Whether or not this error was the result of a unknown error.
     */
    public boolean isUnknownError() {
        return !mNetworkError && !mHttpError && !mConversionError;
    }
}
