package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;

import java.util.List;

// ToDo: consider removing .close() method
public interface AuthenticatedUserStore {
    long insert(@NonNull String userUid, @NonNull String credentials);

    @NonNull
    List<AuthenticatedUserModel> query();

    void close();
}
