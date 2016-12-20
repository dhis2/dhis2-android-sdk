package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;

import java.util.Date;

public interface UserCredentialsStore {
    long insert(
            @NonNull String uid,
            @NonNull String code,
            @NonNull String name,
            @NonNull String displayName,
            @NonNull Date created,
            @NonNull Date lastUpdated,
            @NonNull String username,
            @NonNull String user);

    void close();
}
