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

package org.hisp.dhis.android.core;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.Call;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStoreImpl;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.AuthenticatedUserStoreImpl;
import org.hisp.dhis.android.core.user.IsUserLoggedInCallable;
import org.hisp.dhis.android.core.user.LogOutUserCallable;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserAuthenticateCall;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.hisp.dhis.android.core.user.UserCredentialsStoreImpl;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreImpl;
import org.hisp.dhis.android.core.user.UserService;
import org.hisp.dhis.android.core.user.UserStore;
import org.hisp.dhis.android.core.user.UserStoreImpl;

import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@SuppressWarnings("PMD.ExcessiveImports")
public final class D2 {
    private final Retrofit retrofit;
    private final DbOpenHelper dbOpenHelper;
    private final SQLiteDatabase sqLiteDatabase;

    // services
    private final UserService userService;

    // stores
    private final UserStore userStore;
    private final UserCredentialsStore userCredentialsStore;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final AuthenticatedUserStore authenticatedUserStore;
    private final OrganisationUnitStore organisationUnitStore;
    private final ResourceStore resourceStore;

    @VisibleForTesting
    D2(@NonNull Retrofit retrofit, @NonNull DbOpenHelper dbOpenHelper) {
        this.retrofit = retrofit;
        this.dbOpenHelper = dbOpenHelper;
        this.sqLiteDatabase = dbOpenHelper.getWritableDatabase();

        // services
        this.userService = retrofit.create(UserService.class);

        // stores
        this.userStore =
                new UserStoreImpl(sqLiteDatabase);
        this.userCredentialsStore =
                new UserCredentialsStoreImpl(sqLiteDatabase);
        this.userOrganisationUnitLinkStore =
                new UserOrganisationUnitLinkStoreImpl(sqLiteDatabase);
        this.authenticatedUserStore =
                new AuthenticatedUserStoreImpl(sqLiteDatabase);
        this.organisationUnitStore =
                new OrganisationUnitStoreImpl(sqLiteDatabase);
        this.resourceStore =
                new ResourceStoreImpl(sqLiteDatabase);
    }

    @NonNull
    public Retrofit retrofit() {
        return retrofit;
    }

    @NonNull
    public DbOpenHelper sqliteOpenHelper() {
        return dbOpenHelper;
    }

    @NonNull
    public Call<Response<User>> logIn(@NonNull String username, @NonNull String password) {
        if (username == null) {
            throw new NullPointerException("username == null");
        }
        if (password == null) {
            throw new NullPointerException("password == null");
        }

        return new UserAuthenticateCall(userService, sqLiteDatabase, userStore,
                userCredentialsStore, userOrganisationUnitLinkStore, resourceStore, authenticatedUserStore,
                organisationUnitStore, username, password
        );
    }

    @NonNull
    public Callable<Boolean> isUserLoggedIn() {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        AuthenticatedUserStore authenticatedUserStore =
                new AuthenticatedUserStoreImpl(sqLiteDatabase);

        return new IsUserLoggedInCallable(authenticatedUserStore);
    }

    @NonNull
    public Callable<Void> logOut() {
        return new LogOutUserCallable(
                userStore, userCredentialsStore, userOrganisationUnitLinkStore,
                authenticatedUserStore, organisationUnitStore
        );
    }

    public static class Builder {
        private ConfigurationModel configurationModel;
        private DbOpenHelper dbOpenHelper;
        private OkHttpClient okHttpClient;

        public Builder() {
            // empty constructor
        }

        @NonNull
        public Builder configuration(@NonNull ConfigurationModel configurationModel) {
            this.configurationModel = configurationModel;
            return this;
        }

        @NonNull
        public Builder dbOpenHelper(@NonNull DbOpenHelper dbOpenHelper) {
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
                throw new IllegalArgumentException("dbOpenHelper == null");
            }

            if (configurationModel == null) {
                throw new IllegalStateException("Configuration must be set first");
            }

            if (okHttpClient == null) {
                throw new IllegalArgumentException("okHttpClient == null");
            }

            ObjectMapper objectMapper = new ObjectMapper()
                    .setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw())
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            Converter.Factory jsonConverterFactory =
                    JacksonConverterFactory.create(objectMapper);
            Converter.Factory filterConverterFactory =
                    FilterConverterFactory.create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(configurationModel.serverUrl())
                    .client(okHttpClient)
                    .addConverterFactory(jsonConverterFactory)
                    .addConverterFactory(filterConverterFactory)
                    .validateEagerly(true)
                    .build();

            return new D2(retrofit, dbOpenHelper);
        }
    }
}