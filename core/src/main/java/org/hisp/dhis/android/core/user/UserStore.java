package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public interface UserStore {
    long insert(
            @NonNull String uid, @NonNull String name, @NonNull String displayName,
            @NonNull Date created, @NonNull Date lastUpdated,
            @Nullable String birthday, @Nullable String education, @Nullable String gender,
            @Nullable String jobTitle, @Nullable String surname, @Nullable String firstName,
            @Nullable String introduction, @Nullable String employer, @Nullable String interests,
            @Nullable String languages, @Nullable String email, @Nullable String phoneNumber,
            @Nullable String nationality);

    void close();
}
