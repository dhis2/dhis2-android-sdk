package org.hisp.dhis.android.core.organisationunit;

import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.AuthenticatedUserStoreImpl;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreImpl;
import org.hisp.dhis.android.core.user.UserStore;
import org.hisp.dhis.android.core.user.UserStoreImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Retrofit;

public class OrganisationUnitFactory {
    private final DatabaseAdapter databaseAdapter;
    private final OrganisationUnitService organisationUnitService;
    private final ResourceHandler resourceHandler;

    private final OrganisationUnitStore organisationUnitStore;
    private final UserStore userStore;
    private final OrganisationUnitHandler organisationUnitHandler;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final AuthenticatedUserStore authenticatedUserStore;
    private final List<DeletableStore> deletableStores;

    public OrganisationUnitFactory(
            Retrofit retrofit, DatabaseAdapter databaseAdapter, ResourceHandler resourceHandler) {
        this.databaseAdapter = databaseAdapter;
        this.organisationUnitService = retrofit.create(OrganisationUnitService.class);
        this.resourceHandler = resourceHandler;
        this.organisationUnitStore = new OrganisationUnitStoreImpl(databaseAdapter);
        UserOrganisationUnitLinkStore userOrganisationUnitLinkStore =
                new UserOrganisationUnitLinkStoreImpl(databaseAdapter);
        OrganisationUnitProgramLinkStore organisationUnitProgramLinkStore =
                new OrganisationUnitProgramLinkStoreImpl(databaseAdapter);
        this.organisationUnitHandler = new OrganisationUnitHandler(organisationUnitStore,
                userOrganisationUnitLinkStore, organisationUnitProgramLinkStore, resourceHandler);
        this.userOrganisationUnitLinkStore = new UserOrganisationUnitLinkStoreImpl(databaseAdapter);
        this.authenticatedUserStore = new AuthenticatedUserStoreImpl(databaseAdapter);
        this.userStore = new UserStoreImpl(databaseAdapter);
        deletableStores = new ArrayList<>();
        deletableStores.add(organisationUnitStore);
        deletableStores.add(userOrganisationUnitLinkStore);
        deletableStores.add(organisationUnitProgramLinkStore);
    }

    public OrganisationUnitHandler getOrganisationUnitHandler() {
        return organisationUnitHandler;
    }

    public AuthenticatedUserStore getAuthenticatedUserStore() {
        return authenticatedUserStore;
    }

    public UserOrganisationUnitLinkStore getUserOrganisationUnitLinkStore() {
        return userOrganisationUnitLinkStore;
    }

    public OrganisationUnitCall newEndPointCall(Date serverDate, User user, String uId)
            throws Exception {
        return new OrganisationUnitCall(
                user, organisationUnitService, databaseAdapter,
                resourceHandler, serverDate,
                organisationUnitHandler, uId);
    }

    public UserStore getUserStore() {
        return userStore;
    }

    public List<DeletableStore> getDeletableStores() {
        return deletableStores;
    }

    public ResourceHandler getResourceHandler() {
        return resourceHandler;
    }

    public OrganisationUnitStore getOrganisationUnitStore() {
        return organisationUnitStore;
    }
}
