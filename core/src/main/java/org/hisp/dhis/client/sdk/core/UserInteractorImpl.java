package org.hisp.dhis.client.sdk.core;

import org.hisp.dhis.client.sdk.models.user.User;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserInteractorImpl implements UserInteractor{
    private final Executor callbackExecutor;
    private final UserStore userStore;
    private final UsersApi usersApi;
    private final UserPreferences userPreferences;

    public UserInteractorImpl(Executor callbackExecutor, UsersApi usersApi,
                          UserStore userStore, UserPreferences userPreferences) {
        this.callbackExecutor = callbackExecutor;
        this.userStore = userStore;
        this.usersApi = usersApi;
        this.userPreferences = userPreferences;
    }

    @Override
    public UserStore store() {
        return userStore;
    }

    @Override
    public UsersApi api() {
        return usersApi;
    }

    @Override
    public String username() {
        return userPreferences.getUsername();
    }

    @Override
    public String password() {
        return userPreferences.getPassword();
    }

    @Override
    public Task<User> logIn(String username, String password) {
        return new UserLoginTask(Executors.newCachedThreadPool(),
                callbackExecutor, username, password, usersApi, userStore, userPreferences);
    }

    @Override
    public Object logOut() {
        return null;
    }

    @Override
    public boolean isLoggedIn() {
        return userPreferences.isUserConfirmed();
    }

    @Override
    public void sync() {
        //TODO implement
    }

}
