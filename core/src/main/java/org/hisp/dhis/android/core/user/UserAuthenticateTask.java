package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;

// ToDo: ask about API changes
public final class UserAuthenticateTask {

    @NonNull
    private final UserService userService;

    public UserAuthenticateTask(@NonNull UserService userService) {
        this.userService = userService;
    }

    public UserModel authenticate(@NonNull String username, @NonNull String password) {
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
