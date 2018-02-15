package org.hisp.dhis.android.core.calls;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.event.EventEndPointCall;
import org.hisp.dhis.android.core.event.EventQuery;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.trackedentity.TeiQuery;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;

import java.util.Date;
import java.util.List;

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

    private Response trackerCall() throws Exception {
        Response response = null;

        List<OrganisationUnit> organisationUnits = organisationUnitStore.queryOrganisationUnits();

        int pageSize = TeiQuery.Builder.create().build().getPageSize();

        int numPages = (int) Math.ceil((double) teiLimitByOrgUnit / pageSize);

        int teisDownloaded = 0;

        int pageLimit = 0;

        for (OrganisationUnit orgUnit : organisationUnits) {

            for (int page = 1; page <= numPages; page++) {

                if (page == numPages && teiLimitByOrgUnit > 0) {
                    pageLimit = teiLimitByOrgUnit - teisDownloaded;
                }

                EventQuery eventQuery = EventQuery.
                        Builder.create()
                        .withOrgUnit(orgUnit.uid())
                        .withPage(page)
                        .withPageLimit(pageLimit)
                        .build();

                response = new EventEndPointCall(eventService, databaseAdapter, resourceHandler,
                        eventHandler, serverDate, eventQuery).call();

                if (!response.isSuccessful()) {
                    return response;
                }

                eventsDownloaded = eventsDownloaded + eventQuery.getPageSize();
            }

        }

        return response;
    }

}
