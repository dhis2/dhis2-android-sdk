package org.hisp.dhis.android.core.data.api;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.AuthenticatedUserStoreImpl;

public final class BasicAuthenticatorFactory {
    private BasicAuthenticatorFactory() {
        // no instances
    }

    @NonNull
    public static Authenticator create(@NonNull DbOpenHelper dbOpenHelper) {
        if (dbOpenHelper == null) {
            throw new IllegalArgumentException("dbOpenHelper == null");
        }

        AuthenticatedUserStore authenticatedUserStore = new AuthenticatedUserStoreImpl(
                dbOpenHelper.getWritableDatabase());
        return new BasicAuthenticator(authenticatedUserStore);
    }
}
