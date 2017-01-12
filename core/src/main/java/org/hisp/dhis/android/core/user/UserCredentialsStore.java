package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public interface UserCredentialsStore {
    long insert(
            @NonNull String uid,
            @Nullable String code,
            @Nullable String name,
            @Nullable String displayName,
            @Nullable Date created,
            @Nullable Date lastUpdated,
            @Nullable String username,
            @NonNull String user);

    int delete();

    void close();
}
