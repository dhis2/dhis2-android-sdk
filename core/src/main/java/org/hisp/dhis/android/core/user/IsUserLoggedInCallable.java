package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;

import java.util.concurrent.Callable;

public final class IsUserLoggedInCallable implements Callable<Boolean> {

    @NonNull
    private final AuthenticatedUserStore authenticatedUserStore;

    public IsUserLoggedInCallable(@NonNull AuthenticatedUserStore authenticatedUserStore) {
        this.authenticatedUserStore = authenticatedUserStore;
    }

    @Override
    public Boolean call() throws Exception {
        return !authenticatedUserStore.query().isEmpty();
    }
}
