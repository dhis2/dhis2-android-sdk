package org.hisp.dhis.android.core.relationship;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public interface RelationshipTypeStore {

    long insert(
            @NonNull String uid,
            @Nullable String code,
            @NonNull String name,
            @Nullable String displayName,
            @Nullable Date created,
            @Nullable Date lastUpdated,
            @NonNull String aIsToB,
            @NonNull String bIsToA);

    void close();
}
