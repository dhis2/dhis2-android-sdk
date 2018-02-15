package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class TrackedEntityAttributeEndPointCall implements
        Call<Response<Payload<TrackedEntityAttribute>>> {
    private final TrackedEntityAttributeService trackedEntityAttributeService;
    private final DatabaseAdapter databaseAdapter;
    private final TrackedEntityAttributeQuery trackedEntityAttributeQuery;
    private final Date serverDate;
    private final TrackedEntityAttributeHandler trackedEntityAttributeHandler;
    private final ResourceHandler resourceHandler;

    private boolean isExecuted;

    public TrackedEntityAttributeEndPointCall(
            @NonNull TrackedEntityAttributeService trackedEntityAttributeService,
            @NonNull TrackedEntityAttributeQuery trackedEntityAttributeQuery,
            @NonNull TrackedEntityAttributeHandler trackedEntityAttributeHandler,
            @NonNull ResourceHandler resourceHandler,
            @NonNull DatabaseAdapter databaseAdapter,
            Date serverDate) {
        this.trackedEntityAttributeService = trackedEntityAttributeService;
        this.databaseAdapter = databaseAdapter;
        this.trackedEntityAttributeQuery = trackedEntityAttributeQuery;
        this.serverDate = new Date(serverDate.getTime());
        this.trackedEntityAttributeHandler = trackedEntityAttributeHandler;
        this.resourceHandler = resourceHandler;

        if (trackedEntityAttributeQuery.uIds() != null
                && trackedEntityAttributeQuery.uIds().size() > MAX_UIDS) {
            throw new IllegalArgumentException(
                    "Can't handle the amount of trackedEntityAttributes: "
                            + trackedEntityAttributeQuery.uIds().size() + ". " +
                            "Max size is: " + MAX_UIDS);
        }
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<TrackedEntityAttribute>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }

        String lastSyncedTrackedEntityAttributes = resourceHandler.getLastUpdated(
                ResourceModel.Type.TRACKED_ENTITY_ATTRIBUTE);

        Response<Payload<TrackedEntityAttribute>> trackedEntityAttributeByUids =
                trackedEntityAttributeService.getTrackedEntityAttributes(getFields(),
                        TrackedEntityAttribute.uid.in(trackedEntityAttributeQuery.uIds()),
                        TrackedEntityAttribute.lastUpdated.gt(lastSyncedTrackedEntityAttributes),
                        trackedEntityAttributeQuery.isTranslationOn(),
                        trackedEntityAttributeQuery.translationLocale()).execute();

        if (trackedEntityAttributeByUids.isSuccessful()
                && trackedEntityAttributeByUids.body().items() != null) {

            Transaction transaction = databaseAdapter.beginNewTransaction();

            try {
                List<TrackedEntityAttribute> trackedEntityAttributes =
                        trackedEntityAttributeByUids.body().items();

                for (TrackedEntityAttribute trackedEntityAttribute : trackedEntityAttributes) {
                    trackedEntityAttributeHandler.handleTrackedEntityAttribute(
                            trackedEntityAttribute);
                }
                resourceHandler.handleResource(ResourceModel.Type.TRACKED_ENTITY_ATTRIBUTE,
                        serverDate);
                transaction.setSuccessful();
            } finally {
                transaction.end();
            }
        }
        return trackedEntityAttributeByUids;
    }

    private Fields<TrackedEntityAttribute> getFields() {
        return Fields.<TrackedEntityAttribute>builder().fields(TrackedEntityAttribute.code,
                TrackedEntityAttribute.created, TrackedEntityAttribute.description,
                TrackedEntityAttribute.displayDescription,
                TrackedEntityAttribute.displayInListNoProgram, TrackedEntityAttribute.displayName,
                TrackedEntityAttribute.displayOnVisitSchedule,
                TrackedEntityAttribute.displayShortName,
                TrackedEntityAttribute.generated, TrackedEntityAttribute.inherit,
                TrackedEntityAttribute.lastUpdated, TrackedEntityAttribute.name,
                TrackedEntityAttribute.pattern, TrackedEntityAttribute.programScope,
                TrackedEntityAttribute.shortName,
                TrackedEntityAttribute.sortOrderInListNoProgram, TrackedEntityAttribute.uid,
                TrackedEntityAttribute.unique, TrackedEntityAttribute.valueType).build();
    }
}
