package org.hisp.dhis.android.core;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.api.Authenticator;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;

import okhttp3.OkHttpClient;

public final class D2 {
    public D2() {
    }

    public static class Builder {
        private DbOpenHelper dbOpenHelper;
        private OkHttpClient okHttpClient;

        public Builder() {
            // empty constructor
        }

        @NonNull
        public Builder sqliteOpenHelper(@NonNull DbOpenHelper dbOpenHelper) {
            this.dbOpenHelper = dbOpenHelper;
            return this;
        }

        @NonNull
        public Builder okHttpClient(@NonNull OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public D2 build() {
            if (dbOpenHelper == null) {
                throw new NullPointerException();
            }

            if (okHttpClient == null) {
                // fallback to defaults
                Authenticator.Factory authenticatorFactory =
                        BasicAuthenticatorFactory.create(dbOpenHelper);

                okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(authenticatorFactory.authenticator())
                        .build();
            }

            return new D2();
        }
    }

    private static void main(String[] args) {
        // where to keep base url?

        DbOpenHelper dbOpenHelper = new DbOpenHelper(null, "dhis.db");

        D2 d2 = new D2.Builder()
                .okHttpClient(null)
                .sqliteOpenHelper(dbOpenHelper)
                .build();

        // d2.configure()
        // d2.signIn("username", "password");
        // d2.signOut();
    }
}
