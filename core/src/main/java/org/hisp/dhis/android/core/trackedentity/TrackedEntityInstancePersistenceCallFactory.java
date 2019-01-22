package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModuleDownloader;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.User;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class TrackedEntityInstancePersistenceCallFactory {

    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final TrackedEntityInstanceUidHelper uidsHelper;
    private final ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore;
    private final OrganisationUnitModuleDownloader organisationUnitDownloader;

    @Inject
    TrackedEntityInstancePersistenceCallFactory(
            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
            @NonNull TrackedEntityInstanceUidHelper uidsHelper,
            @NonNull ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore,
            @NonNull OrganisationUnitModuleDownloader organisationUnitDownloader) {
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;
        this.uidsHelper = uidsHelper;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitDownloader = organisationUnitDownloader;
    }

    public Callable<Void> getCall(final List<TrackedEntityInstance> trackedEntityInstances) {

        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                trackedEntityInstanceHandler.handleMany(trackedEntityInstances, false);

                Set<String> searchOrgUnitUids = uidsHelper.getMissingOrganisationUnitUids(trackedEntityInstances);

                if (!searchOrgUnitUids.isEmpty()) {
                    AuthenticatedUserModel authenticatedUserModel = authenticatedUserStore.selectFirst();

                    Callable<List<OrganisationUnit>> organisationUnitCall =
                            organisationUnitDownloader.downloadSearchOrganisationUnits(searchOrgUnitUids,
                                    User.builder().uid(authenticatedUserModel.user()).build());
                    organisationUnitCall.call();
                }

                return null;
            }
        };
    }
}