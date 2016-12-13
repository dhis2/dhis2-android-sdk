package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.models.user.User;

import retrofit2.Call;

// ToDo: ask about API changes
public final class UserAuthenticateTask {

    @NonNull
    private final UserService userService;

    public UserAuthenticateTask(@NonNull UserService userService) {
        this.userService = userService;
    }

    public UserModel authenticate(@NonNull String username, @NonNull String password) {
        Call<User> userCall = userService.me(
                User.UID,
                User.DISPLAY_NAME,
                User.EDUCATION,
                User.ORGANISATION_UNITS.with(
                        OrganisationUnit.UID,
                        OrganisationUnit.DISPLAY_NAME));

        // ToDo: synchronous vs asynchronous


        // Call<User> userCall = userService.user(new HashMap<String, String>());

//        Response<User> userResponse = null;
//        try {
//            userResponse = userCall.execute();
//            User user = userResponse.body();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return null;
    }
}
