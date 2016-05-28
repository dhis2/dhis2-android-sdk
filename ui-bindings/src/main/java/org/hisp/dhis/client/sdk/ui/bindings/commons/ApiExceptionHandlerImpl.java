/*
 *  Copyright (c) 2016, University of Oslo
 *
 *  All rights reserved.
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

package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;

import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.network.Response;
import org.hisp.dhis.client.sdk.ui.bindings.R;
import org.hisp.dhis.client.sdk.utils.Logger;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class ApiExceptionHandlerImpl implements ApiExceptionHandler {
    Context context;
    Logger logger;

    public ApiExceptionHandlerImpl(Context context, Logger logger) {
        this.context = context;
        this.logger = logger;
    }

    public AppError handleException(final String TAG, final Throwable apiException) {
        String title = context.getText(R.string.title_error).toString();
        String message;

        if (apiException instanceof ApiException) {
            int status = -1;
            Response response = ((ApiException) apiException).getResponse();

            if (response != null) {
                status = response.getStatus();
            }

            switch (status) { //Custom error messages :
                case (HttpURLConnection.HTTP_UNAUTHORIZED): {
                    message = context.getText(R.string.error_unauthorized).toString();
                    break;
                }
                case (HttpURLConnection.HTTP_NOT_FOUND): {
                    message = context.getText(R.string.error_not_found).toString();
                    break;
                }
                case (HttpURLConnection.HTTP_BAD_GATEWAY): {
                    title = context.getString(R.string.title_error_unexpected);
                    message = apiException.getMessage();
                    break;
                }
                default: {
                    if (apiException.getCause() instanceof MalformedURLException) {
                        message = context.getText(R.string.error_not_found).toString();
                        break;
                    }
                    title = context.getString(R.string.title_error_unexpected);
                    message = apiException.getMessage();
                    logger.e(TAG, "unexpected error:", apiException);
                }
            }
        } else { //Unexpected error/exception: Thus just default:
            title = context.getString(R.string.title_error_unexpected);
            message = apiException.getMessage();
            logger.e(TAG, "unexpected error:", apiException);
        }
        return new AppError(title, message);
    }
}
