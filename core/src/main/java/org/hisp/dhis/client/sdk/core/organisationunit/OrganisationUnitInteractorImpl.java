package org.hisp.dhis.client.sdk.core.organisationunit;

public class OrganisationUnitInteractorImpl implements OrganisationUnitInteractor {
    private final OrganisationUnitStore organisationUnitStore;

    public OrganisationUnitInteractorImpl(OrganisationUnitStore organisationUnitStore) {
        this.organisationUnitStore = organisationUnitStore;
    }

    @Override
    public OrganisationUnitStore store() {
        return organisationUnitStore;
    }
}
