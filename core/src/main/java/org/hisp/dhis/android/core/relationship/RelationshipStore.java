package org.hisp.dhis.android.core.relationship;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface RelationshipStore {

    long insert(
            @Nullable String trackedEntityInstanceA,
            @Nullable String trackedEntityInstanceB,
            @NonNull String relationshipType
    );

    void close();
}
