package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;

import java.util.List;

public interface AuthenticatedUserStore {
    long save(@NonNull String userUid, @NonNull String credentials);

    @NonNull
    List<AuthenticatedUserModel> query();

    void close();
}
