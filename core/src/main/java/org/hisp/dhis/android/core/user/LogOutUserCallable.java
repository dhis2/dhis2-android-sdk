package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;

import java.util.concurrent.Callable;

public class LogOutUserCallable implements Callable<Void> {

    @NonNull
    private final UserStore userStore;

    @NonNull
    private final UserCredentialsStore userCredentialsStore;

    @NonNull
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;

    @NonNull
    private final AuthenticatedUserStore authenticatedUserStore;

    @NonNull
    private final OrganisationUnitStore organisationUnitStore;

    public LogOutUserCallable(@NonNull UserStore userStore,
            @NonNull UserCredentialsStore userCredentialsStore,
            @NonNull UserOrganisationUnitLinkStore userOrganisationUnitLinkStore,
            @NonNull AuthenticatedUserStore authenticatedUserStore,
            @NonNull OrganisationUnitStore organisationUnitStore) {
        this.userStore = userStore;
        this.userCredentialsStore = userCredentialsStore;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitStore = organisationUnitStore;
    }

    @Override
    public Void call() throws Exception {
        // clear out all tables
        userStore.delete();
        userCredentialsStore.delete();
        userOrganisationUnitLinkStore.delete();
        authenticatedUserStore.delete();
        organisationUnitStore.delete();

        return null;
    }
}
