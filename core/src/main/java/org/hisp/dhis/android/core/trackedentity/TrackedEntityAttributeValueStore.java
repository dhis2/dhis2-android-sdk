package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;

public interface TrackedEntityAttributeValueStore {

    long insert(
            @NonNull State state,
            @Nullable String attribute,
            @Nullable String value
    );

    void close();
}
