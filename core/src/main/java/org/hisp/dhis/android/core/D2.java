package org.hisp.dhis.android.core;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.Call;
import org.hisp.dhis.android.core.configuration.ConfigurationManager;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.Authenticator;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStoreImpl;
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
                userCredentialsStore, userOrganisationUnitLinkStore, authenticatedUserStore,
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
        private ConfigurationManager configurationManager;
        private DbOpenHelper dbOpenHelper;
        private OkHttpClient okHttpClient;

        public Builder() {
            // empty constructor
        }

        @NonNull
        public Builder configurationManager(@NonNull ConfigurationManager configurationManager) {
            this.configurationManager = configurationManager;
            return this;
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
                throw new NullPointerException("dbOpenHelper == null");
            }

            if (okHttpClient == null) {
                // fallback to default solution
                Authenticator.Factory authenticatorFactory =
                        BasicAuthenticatorFactory.create(dbOpenHelper);

                okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(authenticatorFactory.authenticator())
                        .build();
            }

            if (configurationManager == null) {
                throw new NullPointerException("configurationManager == null");
            }

            ConfigurationModel configurationModel = configurationManager.configuration();
            if (configurationModel == null) {
                throw new IllegalStateException("Configuration must be set first");
            }

            ObjectMapper objectMapper = new ObjectMapper()
                    .setDateFormat(BaseIdentifiableObject.DATE_FORMAT)
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
