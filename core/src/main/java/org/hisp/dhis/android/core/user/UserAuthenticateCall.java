package org.hisp.dhis.android.core.user;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.Call;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

import static okhttp3.Credentials.basic;
import static org.hisp.dhis.android.core.data.api.ApiUtils.base64;

// ToDo: ask about API changes
// ToDo: performance tests? Try to feed in a user instance with thousands organisation units
public final class UserAuthenticateCall implements Call<Response<User>> {
    // retrofit service
    private final UserService userService;

    // stores and database related dependencies
    private final SQLiteDatabase database;
    private final UserStore userStore;
    private final UserCredentialsStore userCredentialsStore;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final AuthenticatedUserStore authenticatedUserStore;
    private final OrganisationUnitStore organisationUnitStore;

    // username and password of candidate
    private final String username;
    private final String password;

    private boolean isExecuted;

    public UserAuthenticateCall(
            @NonNull UserService userService,
            @NonNull SQLiteDatabase database,
            @NonNull UserStore userStore,
            @NonNull UserCredentialsStore userCredentialsStore,
            @NonNull UserOrganisationUnitLinkStore userOrganisationUnitLinkStore,
            @NonNull AuthenticatedUserStore authenticatedUserStore,
            @NonNull OrganisationUnitStore organisationUnitStore,
            @NonNull String username,
            @NonNull String password) {
        this.userService = userService;

        this.database = database;
        this.userStore = userStore;
        this.userCredentialsStore = userCredentialsStore;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitStore = organisationUnitStore;

        // credentials
        this.username = username;
        this.password = password;
    }

    @Override
    public Response<User> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }

            isExecuted = true;
        }

        List<AuthenticatedUserModel> authenticatedUsers = authenticatedUserStore.query();
        if (!authenticatedUsers.isEmpty()) {
            throw new IllegalStateException("Another user has already been authenticated: " +
                    authenticatedUsers.get(0));
        }

        Response<User> response = authenticate(basic(username, password));
        if (response.isSuccessful()) {
            saveUser(response.body());
        }

        return response;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    private Response<User> authenticate(String credentials) throws IOException {
        return userService.authenticate(credentials, Filter.<User>builder().fields(
                User.uid, User.code, User.name, User.displayName,
                User.created, User.lastUpdated, User.birthday, User.education,
                User.gender, User.jobTitle, User.surname, User.firstName,
                User.introduction, User.employer, User.interests, User.languages,
                User.email, User.phoneNumber, User.nationality,
                User.userCredentials.with(
                        UserCredentials.uid,
                        UserCredentials.code,
                        UserCredentials.name,
                        UserCredentials.displayName,
                        UserCredentials.created,
                        UserCredentials.lastUpdated,
                        UserCredentials.username),
                User.organisationUnits.with(
                        OrganisationUnit.uid,
                        OrganisationUnit.code,
                        OrganisationUnit.name,
                        OrganisationUnit.displayName,
                        OrganisationUnit.created,
                        OrganisationUnit.lastUpdated,
                        OrganisationUnit.shortName,
                        OrganisationUnit.displayShortName,
                        OrganisationUnit.description,
                        OrganisationUnit.displayDescription,
                        OrganisationUnit.path,
                        OrganisationUnit.openingDate,
                        OrganisationUnit.closedDate,
                        OrganisationUnit.level,
                        OrganisationUnit.parent.with(
                                OrganisationUnit.uid))
        ).build()).execute();
    }

    private Long saveUser(User user) {
        database.beginTransaction();

        Long userId;

        // enclosing transaction in try-finally block in
        // order to make sure that database transaction won't be leaked
        try {
            // insert user model into user table
            userId = userStore.insert(
                    user.uid(), user.code(), user.name(), user.displayName(), user.created(),
                    user.lastUpdated(), user.birthday(), user.education(),
                    user.gender(), user.jobTitle(), user.surname(), user.firstName(),
                    user.introduction(), user.employer(), user.interests(), user.languages(),
                    user.email(), user.phoneNumber(), user.nationality()
            );

            // insert user credentials
            UserCredentials userCredentials = user.userCredentials();
            userCredentialsStore.insert(
                    userCredentials.uid(), userCredentials.code(), userCredentials.name(),
                    userCredentials.displayName(), userCredentials.created(), userCredentials.lastUpdated(),
                    userCredentials.username(), user.uid()
            );

            // insert user as authenticated entity
            authenticatedUserStore.insert(user.uid(), base64(username, password));

            if (user.organisationUnits() != null) {
                for (OrganisationUnit organisationUnit : user.organisationUnits()) {
                    organisationUnitStore.insert(
                            organisationUnit.uid(),
                            organisationUnit.code(),
                            organisationUnit.name(),
                            organisationUnit.displayName(),
                            organisationUnit.created(),
                            organisationUnit.lastUpdated(),
                            organisationUnit.shortName(),
                            organisationUnit.displayShortName(),
                            organisationUnit.description(),
                            organisationUnit.displayDescription(),
                            organisationUnit.path(),
                            organisationUnit.openingDate(),
                            organisationUnit.closedDate(),
                            organisationUnit.parent() == null ? null : organisationUnit.parent().uid(),
                            organisationUnit.level()
                    );

                    // insert link between user and organisation unit
                    userOrganisationUnitLinkStore.insert(
                            user.uid(), organisationUnit.uid(), OrganisationUnitModel.SCOPE_DATA_CAPTURE
                    );
                }
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        return userId;
    }
}