package org.hisp.dhis.android.core.organisationunit;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.AuthenticatedUserStoreImpl;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreImpl;
import org.hisp.dhis.android.core.user.UserStore;
import org.hisp.dhis.android.core.user.UserStoreImpl;

import retrofit2.Retrofit;

public class OrganisationUnitFactory {
    private final DatabaseAdapter databaseAdapter;
    private final OrganisationUnitService optionSetService;
    private final ResourceHandler resourceHandler;
    private final OrganisationUnitStore organisationUnitStore;
    private final OrganisationUnitHandler organisationUnitHandler;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final AuthenticatedUserStore authenticatedUserStore;

    public OrganisationUnitFactory(
            Retrofit retrofit, DatabaseAdapter databaseAdapter, ResourceHandler resourceHandler) {
        this.databaseAdapter = databaseAdapter;
        this.optionSetService = retrofit.create(OrganisationUnitService.class);
        this.resourceHandler = resourceHandler;
        this.organisationUnitStore = new OrganisationUnitStoreImpl(databaseAdapter);
        UserOrganisationUnitLinkStore userOrganisationUnitLinkStore = new UserOrganisationUnitLinkStoreImpl(databaseAdapter);
        OrganisationUnitProgramLinkStore organisationUnitProgramLinkStore = new OrganisationUnitProgramLinkStoreImpl(databaseAdapter);
        this.organisationUnitHandler = new OrganisationUnitHandler(organisationUnitStore, userOrganisationUnitLinkStore, organisationUnitProgramLinkStore);
        this.userOrganisationUnitLinkStore = new UserOrganisationUnitLinkStoreImpl(databaseAdapter);
        this.authenticatedUserStore = new AuthenticatedUserStoreImpl(databaseAdapter);
    }

    public OrganisationUnitHandler getOrganisationUnitHandler(){
        return organisationUnitHandler;
    }

    public AuthenticatedUserStore getAuthenticatedUserStore() {
        return authenticatedUserStore;
    }

    public UserOrganisationUnitLinkStore getUserOrganisationUnitLinkStore(){
        return userOrganisationUnitLinkStore;
    }
}
