package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;

import java.util.Date;

public interface UserStore {
    long insert(
            @NonNull String uid, @NonNull String code, @NonNull String name,
            @NonNull String displayName, @NonNull Date created, @NonNull Date lastUpdated,
            @NonNull String birthday, @NonNull String education, @NonNull String gender,
            @NonNull String jobTitle, @NonNull String surname, @NonNull String firstName,
            @NonNull String introduction, @NonNull String employer, @NonNull String interests,
            @NonNull String languages, @NonNull String email, @NonNull String phoneNumber,
            @NonNull String nationality);

    void close();
}
