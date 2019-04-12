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

package org.hisp.dhis.android.core.arch.api.executors;

import android.util.Log;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public final class APIErrorMapper {

    public D2Error mapThrownException(Throwable t, D2Error.Builder errorBuilder) {
        if (t instanceof SocketTimeoutException) {
            return socketTimeoutException(errorBuilder, (SocketTimeoutException) t);
        } else if (t instanceof UnknownHostException) {
            return unknownHostException(errorBuilder, (UnknownHostException) t);
        } else if (t instanceof IOException) {
            return ioException(errorBuilder, (IOException) t);
        } else if (t instanceof Exception) {
            return unexpectedException(errorBuilder, (Exception) t);
        } else {
            return unexpectedException(errorBuilder, new RuntimeException(t));
        }
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

    private D2Error unexpectedException(D2Error.Builder errorBuilder, Exception e) {
        Log.e(this.getClass().getSimpleName(), e.toString());
        return errorBuilder
                .errorCode(D2ErrorCode.UNEXPECTED)
                .errorDescription("Unexpected exception")
                .originalException(e)
                .build();
    }
}