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

package org.hisp.dhis.android.core.user.internal;

import org.hisp.dhis.android.core.arch.api.executors.internal.APIErrorMapper;
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class LogInCallErrorCatcherShould {

    private UserAuthenticateCallErrorCatcher catcher;

    @Before
    public void setUp() {
        catcher = new UserAuthenticateCallErrorCatcher(ObjectMapperFactory.objectMapper());
    }

    @Test
    public void return_bad_credentials_error_for_expected_error_response() {
        String responseError = "{\"httpStatus\":\"Unauthorized\",\"httpStatusCode\":401,\"status\":\"ERROR\",\"message\":\"Unauthorized\"}";
        Response<Object> response = Response.error(401, ResponseBody.create(null, responseError));
        String errorBody = new APIErrorMapper().getErrorBody(response);
        assertThat(catcher.catchError(response, errorBody)).isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void return_bad_credentials_error_for_other_messages() {
        String responseError = "{\"httpStatus\":\"Unauthorized\",\"httpStatusCode\":401,\"status\":\"ERROR\",\"message\":\"Something new\"}";
        Response<Object> response = Response.error(401, ResponseBody.create(null, responseError));
        String errorBody = new APIErrorMapper().getErrorBody(response);
        assertThat(catcher.catchError(response, errorBody)).isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void return_account_locked() {
        String responseError = "{\"httpStatus\":\"Unauthorized\",\"httpStatusCode\":401,\"status\":\"ERROR\",\"message\":\"Account locked\"}";
        Response<Object> response = Response.error(401, ResponseBody.create(null, responseError));
        String errorBody = new APIErrorMapper().getErrorBody(response);
        assertThat(catcher.catchError(response, errorBody)).isEqualTo(D2ErrorCode.USER_ACCOUNT_LOCKED);
    }

    @Test
    public void return_no_dhis_server_for_another_json() {
        String responseError = "{\"other\":\"JSON\"}";
        Response<Object> response = Response.error(401, ResponseBody.create(null, responseError));
        String errorBody = new APIErrorMapper().getErrorBody(response);
        assertThat(catcher.catchError(response, errorBody)).isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }

    @Test
    public void return_no_dhis_server_for_non_json() {
        String responseError = "<html>ERROR</html>";
        Response<Object> response = Response.error(401, ResponseBody.create(null, responseError));
        String errorBody = new APIErrorMapper().getErrorBody(response);
        assertThat(catcher.catchError(response, errorBody)).isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }
}
