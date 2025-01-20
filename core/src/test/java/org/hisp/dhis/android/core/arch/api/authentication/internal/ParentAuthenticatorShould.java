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

package org.hisp.dhis.android.core.arch.api.authentication.internal;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;
import static okhttp3.Credentials.basic;

import org.hisp.dhis.android.core.arch.api.internal.ServerURLWrapper;
import org.hisp.dhis.android.core.arch.storage.internal.Credentials;
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.UserIdInMemoryStore;
import org.hisp.dhis.android.core.user.internal.ConnectLogoutHandler;
import org.hisp.dhis.android.core.user.openid.OpenIDConnectLogoutHandler;
import org.hisp.dhis.android.core.user.openid.OpenIDConnectTokenRefresher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import io.reactivex.observers.TestObserver;
import kotlin.Unit;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

// ToDo: Solve problem with INFO logs from MockWebServer being interpreted as errors in gradle
@RunWith(JUnit4.class)
public class ParentAuthenticatorShould {

    @Mock
    private CredentialsSecureStore credentialsSecureStore;

    @Mock
    private UserIdInMemoryStore userIdStore;

    @Mock
    private OpenIDConnectTokenRefresher tokenRefresher;

    @Mock
    private OpenIDConnectLogoutHandler logoutHandler;

    private ConnectLogoutHandler connectLogoutHandler;

    private MockWebServer mockWebServer;
    private OkHttpClient okHttpClient;
    private CookieAuthenticatorHelper cookieHelper = new CookieAuthenticatorHelper();

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        mockWebServer = new MockWebServer();

        mockWebServer.start();

        UserIdAuthenticatorHelper userIdHelper = new UserIdAuthenticatorHelper(userIdStore);

        ServerURLWrapper.setServerUrl(mockWebServer.getHostName());
        connectLogoutHandler = new ConnectLogoutHandler(credentialsSecureStore);

        Interceptor authenticator = new ParentAuthenticator(
                credentialsSecureStore,
                new PasswordAndCookieAuthenticator(userIdHelper, cookieHelper, connectLogoutHandler),
                new OpenIDConnectAuthenticator(credentialsSecureStore, tokenRefresher, userIdHelper, logoutHandler),
                cookieHelper
        );
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(authenticator)
                .build();
    }

    @Test
    public void return_test_and_user_when_server_take_request() throws IOException, InterruptedException {
        mockWebServer.enqueue(new MockResponse());

        givenACredentials();

        okHttpClient.newCall(
                        new Request.Builder()
                                .url(mockWebServer.url("/api/me/"))
                                .build())
                .execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getHeader("Authorization"))
                .isEqualTo(basic("test_user", "test_password"));
    }

    @Test
    public void invoke_log_out_if_call_response_is_unauthorized() throws IOException, InterruptedException {
        givenACredentials();
        configureCookie();

        TestObserver<Unit> testObserver = connectLogoutHandler.logOutObservable().test();

        mockWebServer.enqueue(new MockResponse().setResponseCode(401));

        okHttpClient.newCall(
                        new Request.Builder()
                                .url(mockWebServer.url("/api/me/"))
                                .build())
                .execute();

        testObserver.assertValue(Unit.INSTANCE);
    }

    private void givenACredentials() {
        Credentials credentials = new Credentials("test_user", "test_server", "test_password", null);

        when(credentialsSecureStore.get()).thenReturn(credentials);
        when(userIdStore.get()).thenReturn("user-id");
    }

    @Test
    public void return_null_when_server_take_request_with_authenticate_with_empty_list() throws IOException, InterruptedException {
        mockWebServer.enqueue(new MockResponse());

        okHttpClient.newCall(
                        new Request.Builder()
                                .url(mockWebServer.url("/api/me/"))
                                .build())
                .execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getHeader("Authorization")).isNull();
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private void configureCookie() {
        Response response = new Response.Builder()
                .request(new Request.Builder().url(mockWebServer.url("/auth/login/")).build())
                .protocol(Protocol.HTTP_2)
                .code(200)
                .message("success")
                .header("set-cookie", "182718728172817")
                .body(ResponseBody.create("", MediaType.parse("application/json")))
                .build();

        cookieHelper.storeCookieIfSentByServer(response);
    }
}