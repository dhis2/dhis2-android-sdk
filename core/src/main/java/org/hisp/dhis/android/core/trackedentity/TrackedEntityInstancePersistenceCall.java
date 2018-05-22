package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.SearchOrganisationUnitCall;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.AuthenticatedUserStoreImpl;

import java.util.Collection;
import java.util.Set;

import retrofit2.Retrofit;

public class TrackedEntityInstancePersistenceCall extends SyncCall<Void> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final TrackedEntityInstanceUidHelper uidsHelper;
    private final AuthenticatedUserStore authenticatedUserStore;
    private final SearchOrganisationUnitCall.Factory organisationUnitCallFactory;

    private final Collection<TrackedEntityInstance> trackedEntityInstances;

    private TrackedEntityInstancePersistenceCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
            @NonNull TrackedEntityInstanceUidHelper uidsHelper,
            @NonNull AuthenticatedUserStore authenticatedUserStore,
            @NonNull SearchOrganisationUnitCall.Factory organisationUnitCallFactory,
            @NonNull Collection<TrackedEntityInstance> trackedEntityInstances) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;
        this.uidsHelper = uidsHelper;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitCallFactory = organisationUnitCallFactory;
        this.trackedEntityInstances = trackedEntityInstances;
    }

    @Override
    public Void call() throws D2CallException {
        super.setExecuted();

        Transaction transaction = databaseAdapter.beginNewTransaction();

        try {
            trackedEntityInstanceHandler.handleMany(trackedEntityInstances);

            Set<String> searchOrgUnitUids = uidsHelper.getMissingOrganisationUnitUids(trackedEntityInstances);

            if (!searchOrgUnitUids.isEmpty()) {
                AuthenticatedUserModel authenticatedUserModel = authenticatedUserStore.query().get(0);
                SearchOrganisationUnitCall organisationUnitCall = organisationUnitCallFactory.create(
                        databaseAdapter, retrofit, searchOrgUnitUids, authenticatedUserModel.user());
                organisationUnitCall.call();
            }

            transaction.setSuccessful();
        } finally {
            transaction.end();
        }

        return null;
    }

    public static TrackedEntityInstancePersistenceCall create(DatabaseAdapter databaseAdapter,
                                                              Retrofit retrofit,
                                                              Collection<TrackedEntityInstance>
                                                                      trackedEntityInstances) {
        return new TrackedEntityInstancePersistenceCall(
                databaseAdapter,
                retrofit,
                TrackedEntityInstanceHandler.create(databaseAdapter),
                TrackedEntityInstanceUidHelperImpl.create(databaseAdapter),
                new AuthenticatedUserStoreImpl(databaseAdapter),
                SearchOrganisationUnitCall.FACTORY,
                trackedEntityInstances
        );
    }
}
