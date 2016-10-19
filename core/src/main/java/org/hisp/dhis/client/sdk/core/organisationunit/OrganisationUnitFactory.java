package org.hisp.dhis.client.sdk.core.organisationunit;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

public final class OrganisationUnitFactory {
    private OrganisationUnitFactory() {
        // no instances
    }

    public static OrganisationUnitInteractor create(@NonNull ContentResolver contentResolver) {
        OrganisationUnitStore organisationUnitStore =
                new OrganisationUnitStoreImpl(contentResolver);
        return new OrganisationUnitInteractorImpl(organisationUnitStore);
    }
}
