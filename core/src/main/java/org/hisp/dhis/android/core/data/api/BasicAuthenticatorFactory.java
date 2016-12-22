package org.hisp.dhis.android.core.data.api;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.AuthenticatedUserStoreImpl;

public final class BasicAuthenticatorFactory implements Authenticator.Factory {
    private final DbOpenHelper dbOpenHelper;

    public static Authenticator.Factory create(@NonNull DbOpenHelper dbOpenHelper) {
        if (dbOpenHelper == null) {
            throw new NullPointerException("dbOpenHelper == null");
        }

        return new BasicAuthenticatorFactory(dbOpenHelper);
    }

    private BasicAuthenticatorFactory(DbOpenHelper dbOpenHelper) {
        this.dbOpenHelper = dbOpenHelper;
    }

    @Override
    public Authenticator authenticator() {
        AuthenticatedUserStore authenticatedUserStore = new AuthenticatedUserStoreImpl(
                dbOpenHelper.getWritableDatabase());
        return new BasicAuthenticator(authenticatedUserStore);
    }
}
