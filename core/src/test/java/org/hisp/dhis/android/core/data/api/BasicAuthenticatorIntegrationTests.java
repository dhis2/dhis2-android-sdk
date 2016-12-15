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

@RunWith(JUnit4.class)
public class BasicAuthenticatorIntegrationTests {

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
    public void authenticator_shouldAddAuthorizationHeader() throws IOException, InterruptedException {
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
    public void authenticator_shouldNotModifyRequestIfNoUsers() throws IOException, InterruptedException {
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
