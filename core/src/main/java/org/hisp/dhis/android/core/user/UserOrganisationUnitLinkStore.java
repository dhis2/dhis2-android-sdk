package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;

public interface UserOrganisationUnitLinkStore {

    long insert(
            @NonNull String user,
            @NonNull String organisationUnit,
            @NonNull String organisationUnitScope
    );

    int delete();

    void close();
}
