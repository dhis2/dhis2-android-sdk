package org.hisp.dhis.android.core.data.api;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import okhttp3.Response;

final class BasicAuthenticator implements Authenticator {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC_CREDENTIALS = "Basic %s";

    private final AuthenticatedUserStore authenticatedUserStore;

    BasicAuthenticator(@NonNull AuthenticatedUserStore authenticatedUserStore) {
        this.authenticatedUserStore = authenticatedUserStore;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String authorizationHeader = chain.request().header(AUTHORIZATION);
        if (authorizationHeader != null) {
            // authorization header has already been set
            return chain.proceed(chain.request());
        }

        List<AuthenticatedUserModel> authenticatedUsers =
                authenticatedUserStore.query();
        if (authenticatedUsers.isEmpty()) {
            // proceed request if we do not
            // have any users authenticated
            return chain.proceed(chain.request());
        }

        // retrieve first user and pass in his / her credentials
        Request request = chain.request().newBuilder()
                .addHeader(AUTHORIZATION, String.format(Locale.US,
                        BASIC_CREDENTIALS, authenticatedUsers.get(0).credentials()))
                .build();
        return chain.proceed(request);
    }
}
