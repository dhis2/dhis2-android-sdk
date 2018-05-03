package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.event.EventEndPointCall;
import org.hisp.dhis.android.core.event.EventQuery;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;

import java.util.Set;

import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public final class SingleDataCall extends SyncCall<Response> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;

    private final IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore;

    private final int eventLimitByOrgUnit;

    private SingleDataCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore,
            int eventLimitByOrgUnit) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.organisationUnitStore = organisationUnitStore;

        this.eventLimitByOrgUnit = eventLimitByOrgUnit;
    }

    @Override
    public Response call() throws Exception {
        super.setExecuted();

        Response response = null;
        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            response = SystemInfoCall.FACTORY.create(databaseAdapter, retrofit).call();

            if (!response.isSuccessful()) {
                return response;
            }

            SystemInfo systemInfo = (SystemInfo) response.body();
            GenericCallData genericCallData = GenericCallData.create(databaseAdapter, retrofit,
                    systemInfo.serverDate());

            response = eventCall(genericCallData);

            if (response == null || !response.isSuccessful()) {
                return response;
            }

            transaction.setSuccessful();
            return response;
        } finally {
            transaction.end();
        }
    }

    private Response eventCall(GenericCallData genericCallData) throws Exception {
        Response response = null;

        Set<String> organisationUnitUids = organisationUnitStore.selectUids();

        //TODO: we should download events by orgunit and program
        //programs to retrieve from DB should be non tracker programs
        //TrackerPrograms: programType = WITH_REGISTRATION
        //Non TrackerPrograms: programType = WITHOUT_REGISTRATION

        int pageSize = EventQuery.Builder.create().build().getPageSize();

        int numPages = (int) Math.ceil((double) eventLimitByOrgUnit / pageSize);

        int eventsDownloaded = 0;

        int pageLimit = 0;

        for (String orgUnitUid : organisationUnitUids) {

            for (int page = 1; page <= numPages; page++) {

                if (page == numPages && eventLimitByOrgUnit > 0) {
                    pageLimit = eventLimitByOrgUnit - eventsDownloaded;
                }

                EventQuery eventQuery = EventQuery.
                        Builder.create()
                        .withOrgUnit(orgUnitUid)
                        .withPage(page)
                        .withPageLimit(pageLimit)
                        .build();

                response = EventEndPointCall.create(genericCallData, eventQuery).call();

                if (!response.isSuccessful()) {
                    return response;
                }

                eventsDownloaded = eventsDownloaded + eventQuery.getPageSize();
            }

        }

        return response;
    }

    public static SingleDataCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit,
                                        int eventLimitByOrgUnit) {
        return new SingleDataCall(
                databaseAdapter,
                retrofit,
                OrganisationUnitStore.create(databaseAdapter),
                eventLimitByOrgUnit
        );
    }
}
