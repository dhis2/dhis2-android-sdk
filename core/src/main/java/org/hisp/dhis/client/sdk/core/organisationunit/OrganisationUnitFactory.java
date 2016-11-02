package org.hisp.dhis.client.sdk.core.organisationunit;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Retrofit;

public final class OrganisationUnitFactory {
    private OrganisationUnitFactory() {
        // no instances
    }

    public static OrganisationUnitInteractor create(@NonNull Retrofit retrofit, @NonNull ContentResolver contentResolver,
                                                    @NonNull ObjectMapper objectMapper) {
        OrganisationUnitStore organisationUnitStore =
                new OrganisationUnitStoreImpl(contentResolver, objectMapper);

        OrganisationUnitsApi organisationUnitsApi = retrofit.create(OrganisationUnitsApi.class);

        return new OrganisationUnitInteractorImpl(organisationUnitStore, organisationUnitsApi);
    }
}
