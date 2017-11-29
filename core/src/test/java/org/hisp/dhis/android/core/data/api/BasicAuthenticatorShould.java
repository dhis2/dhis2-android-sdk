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

package org.hisp.dhis.android.core.data.api;

import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static okhttp3.Credentials.basic;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.data.api.ApiUtils.base64;
import static org.mockito.Mockito.when;

// ToDo: Solve problem with INFO logs from MockWebServer being interpreted as errors in gradle
@RunWith(JUnit4.class)
public class BasicAuthenticatorShould {

    @Mock
    private AuthenticatedUserStore authenticatedUserStore;

    private MockWebServer mockWebServer;
    private OkHttpClient okHttpClient;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse());
        mockWebServer.start();

        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthenticator(authenticatedUserStore))
                .build();
    }

    @Test
    public void return_test_and_user_when_server_take_request() throws IOException, InterruptedException {
        AuthenticatedUserModel authenticatedUserModel =
                AuthenticatedUserModel.builder()
                        .user("test_user")
                        .credentials(base64("test_user", "test_password"))
                        .build();

        when(authenticatedUserStore.query()).thenReturn(Arrays.asList(authenticatedUserModel));

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
    public void return_null_when_server_take_request_with_authenticate_with_empty_list() throws IOException, InterruptedException {
        when(authenticatedUserStore.query()).thenReturn(new ArrayList<AuthenticatedUserModel>());

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
}
