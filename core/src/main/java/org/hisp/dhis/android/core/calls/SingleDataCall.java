package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.event.EventEndPointCall;
import org.hisp.dhis.android.core.event.EventHandler;
import org.hisp.dhis.android.core.event.EventQuery;
import org.hisp.dhis.android.core.event.EventService;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.systeminfo.SystemInfoQuery;
import org.hisp.dhis.android.core.systeminfo.SystemInfoService;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;

import java.util.Date;
import java.util.List;

import retrofit2.Response;

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class SingleDataCall implements Call<Response> {

    private final OrganisationUnitStore organisationUnitStore;
    private final SystemInfoStore systemInfoStore;
    private final SystemInfoService systemInfoService;
    private final ResourceStore resourceStore;

    private final EventService eventService;
    private final DatabaseAdapter databaseAdapter;
    private final ResourceHandler resourceHandler;
    private final EventHandler eventHandler;

    private final int eventLimitByOrgUnit;

    private boolean isExecuted;
    private final boolean isTranslationOn;
    private final String translationLocale;

    public SingleDataCall(
            @NonNull OrganisationUnitStore organisationUnitStore,
            @NonNull SystemInfoStore systemInfoStore,
            @NonNull SystemInfoService systemInfoService,
            @NonNull ResourceStore resourceStore,
            @NonNull EventService eventService,
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull ResourceHandler resourceHandler,
            @NonNull EventHandler eventHandler,
            int eventLimitByOrgUnit, boolean isTranslationOn,
            @NonNull String translationLocale) {

        this.organisationUnitStore = organisationUnitStore;

        this.systemInfoStore = systemInfoStore;
        this.systemInfoService = systemInfoService;
        this.resourceStore = resourceStore;

        this.eventService = eventService;
        this.databaseAdapter = databaseAdapter;
        this.resourceHandler = resourceHandler;
        this.eventHandler = eventHandler;
        this.eventLimitByOrgUnit = eventLimitByOrgUnit;
        this.isTranslationOn = isTranslationOn;
        this.translationLocale = translationLocale;
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

        Response response;
        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            SystemInfoQuery systemInfoQuery = SystemInfoQuery.defaultQuery(isTranslationOn,
                    translationLocale);
            response = new SystemInfoCall(
                    databaseAdapter, systemInfoStore,
                    systemInfoService, resourceStore,
                    systemInfoQuery
            ).call();

            if (!response.isSuccessful()) {
                return response;
            }

            SystemInfo systemInfo = (SystemInfo) response.body();
            Date serverDate = systemInfo.serverDate();

            response = eventCall(serverDate);

            if (response == null || !response.isSuccessful()) {
                return response;
            }

            transaction.setSuccessful();
            return response;
        } finally {
            transaction.end();
        }
    }

    private Response eventCall(Date serverDate) throws Exception {
        Response response = null;

        List<OrganisationUnit> organisationUnits = organisationUnitStore.queryOrganisationUnits();

        //TODO: we should download events by orgunit and program
        //programs to retrieve from DB should be non tracker programs
        //TrackerPrograms: programType = WITH_REGISTRATION
        //Non TrackerPrograms: programType = WITHOUT_REGISTRATION

        int pageSize = EventQuery.Builder.create().build().pageSize();

        int numPages = (int) Math.ceil((double) eventLimitByOrgUnit / pageSize);

        int eventsDownloaded = 0;

        int pageLimit = 0;

        for (OrganisationUnit orgUnit : organisationUnits) {

            for (int page = 1; page <= numPages; page++) {

                if (page == numPages && eventLimitByOrgUnit > 0) {
                    pageLimit = eventLimitByOrgUnit - eventsDownloaded;
                }

                EventQuery eventQuery = EventQuery.
                        Builder.create()
                        .withOrgUnit(orgUnit.uid())
                        .withPage(page)
                        .withPageLimit(pageLimit)
                        .withIsTranslationOn(isTranslationOn)
                        .withTranslationLocale(translationLocale)
                        .build();

                response = new EventEndPointCall(eventService, databaseAdapter, resourceHandler,
                        eventHandler, serverDate, eventQuery).call();

                if (!response.isSuccessful()) {
                    return response;
                }

                eventsDownloaded = eventsDownloaded + eventQuery.pageSize();
            }

        }

        return response;
    }
}
