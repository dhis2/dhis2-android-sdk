package org.hisp.dhis.client.sdk.core.organisationunit;

public class OrganisationUnitInteractorImpl implements OrganisationUnitInteractor {
    private final OrganisationUnitStore organisationUnitStore;
    private final OrganisationUnitsApi organisationUnitsApi;

    public OrganisationUnitInteractorImpl(OrganisationUnitStore organisationUnitStore, OrganisationUnitsApi organisationUnitsApi) {
        this.organisationUnitStore = organisationUnitStore;
        this.organisationUnitsApi = organisationUnitsApi;
    }

    @Override
    public OrganisationUnitStore store() {
        return organisationUnitStore;
    }

    @Override
    public OrganisationUnitsApi api() {
        return organisationUnitsApi;
    }
}
