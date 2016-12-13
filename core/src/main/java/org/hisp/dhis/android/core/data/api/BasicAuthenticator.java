package org.hisp.dhis.android.core.data.api;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

import static okhttp3.Credentials.basic;

final class BasicAuthenticator implements Authenticator {
    private static final String AUTHORIZATION = "Authorization";
    private final CredentialsProvider credentialsProvider;

    public BasicAuthenticator(@NonNull CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader(AUTHORIZATION, basic(
                        credentialsProvider.username(),
                        credentialsProvider.password()))
                .build();
        return chain.proceed(request);
    }
}
