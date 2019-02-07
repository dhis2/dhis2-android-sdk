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

package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class UserAuthenticateCallErrorCatcherShould {

    private UserAuthenticateCallErrorCatcher catcher;

    @Before
    public void setUp() throws Exception {
        catcher = new UserAuthenticateCallErrorCatcher();
    }

    @Test
    public void return_bad_credentials_error_in_2_30() throws Exception {

        String responseError = "<!doctype html><html lang=\"en\"><head><title>HTTP Status 401 – Unauthorized</title></head><body><h1>HTTP Status 401 – Unauthorized</h1><hr class=\"line\" /><p><b>Type</b> Status Report</p><p><b>Message</b> LDAP authentication is not configured</p><p><b>Description</b> The request has not been applied because it lacks valid authentication credentials for the target resource.</p><hr class=\"line\" /><h3>Apache Tomcat/8.5.24</h3></body></html>";
        Response response = Response.error(409, ResponseBody.create(null, responseError));

        assertThat(catcher.catchError(response)).isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void return_bad_credentials_error_in_2_29() throws Exception {

        String responseError = "<!doctype html><html lang=\"en\"><head><title>HTTP Status 401 – Unauthorized</title></head><body><h1>HTTP Status 401 – Unauthorized</h1><hr class=\"line\" /><p><b>Type</b> Status Report</p><p><b>Message</b> Bad credentials</p><p><b>Description</b> The request has not been applied because it lacks valid authentication credentials for the target resource.</p><hr class=\"line\" /><h3>Apache Tomcat/8.5.24</h3></body></html>";
        Response response = Response.error(409, ResponseBody.create(null, responseError));

        assertThat(catcher.catchError(response)).isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void return_user_account_disabled() throws Exception {

        String responseError = "<!doctype html><html lang=\"en\"><head><title>HTTP Status 401 – Unauthorized</title></head><body><h1>HTTP Status 401 – Unauthorized</h1><hr class=\"line\" /><p><b>Type</b> Status Report</p><p><b>Message</b> User is disabled</p><p><b>Description</b> The request has not been applied because it lacks valid authentication credentials for the target resource.</p><hr class=\"line\" /><h3>Apache Tomcat/8.5.24</h3></body></html>";
        Response response = Response.error(409, ResponseBody.create(null, responseError));

        assertThat(catcher.catchError(response)).isEqualTo(D2ErrorCode.USER_ACCOUNT_DISABLED);
    }

    @Test
    public void return_user_account_locked() throws Exception {

        String responseError = "<!doctype html><html lang=\"en\"><head><title>HTTP Status 401 – Unauthorized</title></head><body><h1>HTTP Status 401 – Unauthorized</h1><hr class=\"line\" /><p><b>Type</b> Status Report</p><p><b>Message</b> User account is locked</p><p><b>Description</b> The request has not been applied because it lacks valid authentication credentials for the target resource.</p><hr class=\"line\" /><h3>Apache Tomcat/8.5.24</h3></body></html>";
        Response response = Response.error(409, ResponseBody.create(null, responseError));

        assertThat(catcher.catchError(response)).isEqualTo(D2ErrorCode.USER_ACCOUNT_LOCKED);
    }

    @Test
    public void return_null_if_there_is_no_matches() throws Exception {

        String responseError = "";
        Response response = Response.error(409, ResponseBody.create(null, responseError));

        assertThat(catcher.catchError(response)).isEqualTo(null);
    }

    @Test
    public void return_bad_url_if_not_found() throws Exception {

        String responseError = "<html><head><title>404 Not Found</title></head><body bgcolor=\"white\"><center><h1>404 Not Found</h1></center><hr><center>nginx/1.14.0</center></body></html>";
        Response response = Response.error(404, ResponseBody.create(null, responseError));

        assertThat(catcher.catchError(response)).isEqualTo(D2ErrorCode.URL_NOT_FOUND);
    }
}
