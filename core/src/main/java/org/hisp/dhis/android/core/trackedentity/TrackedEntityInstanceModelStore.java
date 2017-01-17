package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;

import java.util.Date;

public interface TrackedEntityInstanceModelStore {
    long insert(
            @NonNull String uid, @Nullable Date created, @Nullable Date lastUpdated,
            @NonNull String organisationUnit, @Nullable State state);

    int delete();

    void close();
}
