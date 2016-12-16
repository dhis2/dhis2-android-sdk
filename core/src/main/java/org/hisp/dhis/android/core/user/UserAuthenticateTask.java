package org.hisp.dhis.android.core.user;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.Callback;
import org.hisp.dhis.android.core.common.Task;
import org.hisp.dhis.android.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.models.user.User;
import org.hisp.dhis.android.models.user.UserCredentials;

import java.io.IOException;
import java.util.concurrent.Executor;

import retrofit2.Response;

import static okhttp3.Credentials.basic;
import static org.hisp.dhis.android.core.data.api.ApiUtils.base64;

// ToDo: ask about API changes
public final class UserAuthenticateTask implements Task<Long> {
    private final Executor executor;
    private final SQLiteDatabase database;
    private final UserService userService;

    // stores
    private final UserStore userStore;
    private final UserCredentialsStore userCredentialsStore;
    private final AuthenticatedUserStore authenticatedUserStore;

    // username and password of candidate
    private final String username;
    private final String password;

    private boolean isCanceled;
    private boolean isExecuted;

    // ToDo: ContentResolver has to push notifications through URI in order to notify listeners

    public UserAuthenticateTask(
            @NonNull Executor executor,
            @NonNull UserService userService,
            @NonNull UserStore userStore,
            @NonNull UserCredentialsStore userCredentialsStore,
            @NonNull AuthenticatedUserStore authenticatedUserStore,
            @NonNull String username,
            @NonNull String password) {
        this.executor = executor;
        this.userService = userService;

        this.userStore = userStore;
        this.userCredentialsStore = userCredentialsStore;
        this.authenticatedUserStore = authenticatedUserStore;

        // credentials
        this.username = username;
        this.password = password;

        this.database = SQLiteDatabase.create(null);
    }

    @Override
    public Long execute() throws RuntimeException {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalArgumentException("Already executed");
            }

            isExecuted = true;
        }

        try {
            Response<User> response = authenticateUser(basic(username, password));

            // if not success, clear-up credentials
            if (response.isSuccessful()) {
                saveUser(response.body());
            }
        } catch (IOException ioException) {
            // wrapping and re-throwing exception
            throw new RuntimeException(ioException);
        }

        return null;
    }

    @Override
    public void execute(final Callback<Long> callback) {
        if (callback == null) {
            throw new NullPointerException("callback must not be null");
        }

        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }

            isExecuted = true;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<User> response = authenticateUser(basic(username, password));

                    // if not success, clear-up credentials
                    if (response.isSuccessful()) {
                        saveUser(response.body());
                    }

                    callback.onSuccess(UserAuthenticateTask.this, null);
                } catch (Throwable throwable) {
                    callback.onFailure(UserAuthenticateTask.this, throwable);
                }
            }
        });
    }

    @Override
    public synchronized boolean isExecuted() {
        return isExecuted;
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    private Response<User> authenticateUser(String credentials) throws IOException {
        return userService.authenticate(credentials,
                User.uid, User.code, User.name, User.displayName, User.created, User.lastUpdated,
                User.birthday, User.education, User.gender, User.jobTitle, User.surname,
                User.firstName, User.introduction, User.employer, User.interests, User.languages,
                User.email, User.phoneNumber, User.nationality,

                // ToDo: adapt userCredentials
                User.userCredentials.with(),
                User.organisationUnits.with(OrganisationUnit.UID),
                User.teiSearchOrganisationUnits.with(OrganisationUnit.UID),
                User.dataViewOrganisationUnits.with(OrganisationUnit.UID)
        ).execute();
    }

    // ToDo: clean-up resources (close statements and database)
    private long saveUser(User user) {
        database.beginTransaction();

        long userId;

        // enclosing transaction in try-finally block in
        // order to make sure that database won't be leaked
        try {
            // insert user model into user table
            userId = userStore.insert(
                    user.uid(), user.code(), user.name(), user.displayName(),
                    user.created(), user.lastUpdated(), user.birthday(), user.education(),
                    user.gender(), user.jobTitle(), user.surname(), user.firstName(),
                    user.introduction(), user.employer(), user.interests(), user.languages(),
                    user.email(), user.phoneNumber(), user.nationality()
            );

            // insert user credentials
            UserCredentials userCredentials = user.userCredentials();
            userCredentialsStore.insert(
                    userCredentials.uid(), userCredentials.code(), user.name(), user.displayName(),
                    user.created(), user.lastUpdated(), userCredentials.username(), user.uid()
            );

            // insert user as authenticated entity
            authenticatedUserStore.insert(user.uid(), base64(username, password));

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        database.close();

        return userId;
    }
}
