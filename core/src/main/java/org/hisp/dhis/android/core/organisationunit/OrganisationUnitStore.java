package org.hisp.dhis.android.core.organisationunit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public interface OrganisationUnitStore {
    long insert(
            @NonNull String uid, @NonNull String code, @NonNull String name,
            @NonNull String displayName, @NonNull Date created, @NonNull Date lastUpdated,
            @NonNull String shortName, @NonNull String displayShortName,
            @NonNull String description, @NonNull String displayDescription,
            @NonNull String path, @NonNull Date openingDate, @NonNull Date closedDate,
            @Nullable String parent, int level
    );

    void close();
}
