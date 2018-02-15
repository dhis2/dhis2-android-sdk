package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.systeminfo.SystemInfoQuery;
import org.hisp.dhis.android.core.systeminfo.SystemInfoService;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEndPointCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;

import java.util.Date;
import java.util.Map;

import retrofit2.Response;

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class TrackerDataCall implements Call<Response> {

    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final SystemInfoStore systemInfoStore;
    private final SystemInfoService systemInfoService;
    private final ResourceStore resourceStore;

    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final DatabaseAdapter databaseAdapter;
    private final ResourceHandler resourceHandler;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final boolean isTranslationOn;
    private final String translationLocale;

    private boolean isExecuted;

    public TrackerDataCall(
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull SystemInfoStore systemInfoStore,
            @NonNull SystemInfoService systemInfoService,
            @NonNull ResourceStore resourceStore,
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull ResourceHandler resourceHandler,
            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
            boolean isTranslationOn,
            @NonNull String translationLocale) {

        this.trackedEntityInstanceStore = trackedEntityInstanceStore;

        this.systemInfoStore = systemInfoStore;
        this.systemInfoService = systemInfoService;
        this.resourceStore = resourceStore;

        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.databaseAdapter = databaseAdapter;
        this.resourceHandler = resourceHandler;
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;
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

        Response response = null;

        Map<String, TrackedEntityInstance> trackedEntityInstances =
                trackedEntityInstanceStore.querySynced();

        if (!trackedEntityInstances.isEmpty()) {
            Transaction transaction = databaseAdapter.beginNewTransaction();
            SystemInfoQuery systemInfoQuery = SystemInfoQuery.defaultQuery(isTranslationOn,
                    translationLocale);

            try {

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

                response = trackedEntityInstanceCall(serverDate, trackedEntityInstances);

                transaction.setSuccessful();

            } finally {
                transaction.end();
            }
        }

        return response;
    }

    private Response trackedEntityInstanceCall(Date serverDate,
            Map<String, TrackedEntityInstance> trackedEntityInstances) throws Exception {
        Response response = null;

        for (Map.Entry<String, TrackedEntityInstance> entry : trackedEntityInstances.entrySet()) {

            response = new TrackedEntityInstanceEndPointCall(trackedEntityInstanceService,
                    databaseAdapter, trackedEntityInstanceHandler, resourceHandler,
                    serverDate, entry.getValue().uid(), isTranslationOn, translationLocale).call();

            if (!response.isSuccessful()) {
                return response;
            }
        }

        return response;
    }
}
