package org.hisp.dhis.android.core.organisationunit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public interface OrganisationUnitStore {
    long insert(
            @NonNull String uid, @Nullable String code, @Nullable String name,
            @Nullable String displayName, @Nullable Date created, @Nullable Date lastUpdated,
            @Nullable String shortName, @Nullable String displayShortName,
            @Nullable String description, @Nullable String displayDescription,
            @Nullable String path, @Nullable Date openingDate, @Nullable Date closedDate,
            @Nullable String parent, @Nullable Integer level
    );

    int delete();

    void close();
}
