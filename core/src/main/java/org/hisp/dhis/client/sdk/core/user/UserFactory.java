package org.hisp.dhis.client.sdk.core.user;

import android.app.Application;
import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Retrofit;

public final class UserFactory {
    private UserFactory() {
        // no instances
    }

    public static UserPreferences create(@NonNull Application application) {
        return new UserPreferencesImpl(application);
    }

    public static UserInteractor create(@NonNull UserPreferences userPreferences,
            @NonNull Retrofit retrofit, @NonNull ContentResolver contentResolver,
            @NonNull ObjectMapper objectMapper) {
        // user interactor
        UserStore userStore = new UserStoreImpl(contentResolver, objectMapper);
        UsersApi usersApi = retrofit.create(UsersApi.class);
        return new UserInteractorImpl(usersApi, userStore, userPreferences);
    }
}
