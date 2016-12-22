package org.hisp.dhis.android.core.organisationunit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public interface OrganisationUnitStore {
    long insert(
            @NonNull String uid, @NonNull String code, @NonNull String name,
            @NonNull String displayName, @NonNull Date created, @NonNull Date lastUpdated,
            @Nullable String shortName, @Nullable String displayShortName,
            @Nullable String description, @Nullable String displayDescription,
            @Nullable String path, @Nullable Date openingDate, @Nullable Date closedDate,
            @Nullable String parent, @Nullable Integer level
    );

    void close();
}
