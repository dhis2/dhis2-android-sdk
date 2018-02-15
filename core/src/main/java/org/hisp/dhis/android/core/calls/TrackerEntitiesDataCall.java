package org.hisp.dhis.android.core.calls;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;

import retrofit2.Response;

public class TrackerEntitiesDataCall implements Call<Response> {

    private boolean isExecuted;
    private final int teiLimitByOrgUnit;
    private final OrganisationUnitStore organisationUnitStore;
    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final DatabaseAdapter databaseAdapter;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;

    public TrackerEntitiesDataCall(@NonNull OrganisationUnitStore organisationUnitStore,
                                   @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
                                   @NonNull DatabaseAdapter databaseAdapter,
                                   @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
                                   int teiLimitByOrgUnit) {

        this.teiLimitByOrgUnit = teiLimitByOrgUnit;
        this.organisationUnitStore = organisationUnitStore;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.databaseAdapter = databaseAdapter;
        this.trackedEntityInstanceHandler =  trackedEntityInstanceHandler;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }

        Response response = null;

        Transaction transaction = databaseAdapter.beginNewTransaction();

        try {



            return response;
        } finally {
            transaction.end();
        }
    }

}
