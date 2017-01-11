package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public interface UserStore {
    long insert(
            @NonNull String uid, @Nullable String code, @Nullable String name, @Nullable String displayName,
            @Nullable Date created, @Nullable Date lastUpdated,
            @Nullable String birthday, @Nullable String education, @Nullable String gender,
            @Nullable String jobTitle, @Nullable String surname, @Nullable String firstName,
            @Nullable String introduction, @Nullable String employer, @Nullable String interests,
            @Nullable String languages, @Nullable String email, @Nullable String phoneNumber,
            @Nullable String nationality);

    int delete();

    void close();
}
