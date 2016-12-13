package org.hisp.dhis.android.core.data.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static okhttp3.Credentials.basic;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class BasicAuthenticatorIntegrationTests {

    @Test
    public void authenticator_shouldAddAuthorizationHeader() throws IOException, InterruptedException {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse());
        mockWebServer.start();

        CredentialsProvider credentialsProviderMock = mock(CredentialsProvider.class);
        when(credentialsProviderMock.username()).thenReturn("test_user");
        when(credentialsProviderMock.password()).thenReturn("test_password");

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthenticator(credentialsProviderMock))
                .build();

        okHttpClient.newCall(
                new Request.Builder()
                        .url(mockWebServer.url("/api/me/"))
                        .build())
                .execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getHeader("Authorization"))
                .isEqualTo(basic("test_user", "test_password"));

        mockWebServer.shutdown();
    }
}
