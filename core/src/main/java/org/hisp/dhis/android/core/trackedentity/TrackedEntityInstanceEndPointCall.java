package org.hisp.dhis.android.core.trackedentity;

import static org.hisp.dhis.android.core.resource.ResourceModel.Type.TRACKED_ENTITY_INSTANCE;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.Date;

import retrofit2.Response;

public class TrackedEntityInstanceEndPointCall implements
        Call<Response<Payload<TrackedEntityInstance>>> {

    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final DatabaseAdapter databaseAdapter;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final ResourceHandler resourceHandler;
    private final Date serverDate;
    private final String trackedEntityInstanceUid;
    private final boolean isTranslationOn;
    private final String translationLocale;
    private boolean isExecuted;

    public TrackedEntityInstanceEndPointCall(
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
            @NonNull ResourceHandler resourceHandler,
            @NonNull Date serverDate,
            @NonNull String trackedEntityInstanceUid, boolean isTranslationOn,
            @NonNull String translationLocale) {
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.databaseAdapter = databaseAdapter;
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;
        this.resourceHandler = resourceHandler;
        this.serverDate = new Date(serverDate.getTime());

        if (trackedEntityInstanceUid == null || trackedEntityInstanceUid.isEmpty()) {
            throw new IllegalArgumentException(
                    "trackedEntityInstanceUid is required to realize a request");
        }

        this.trackedEntityInstanceUid = trackedEntityInstanceUid;
        this.isTranslationOn = isTranslationOn;
        this.translationLocale = translationLocale;
    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public Response call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }

            isExecuted = true;
        }

        Response<TrackedEntityInstance> response =
                trackedEntityInstanceService.trackedEntityInstance(trackedEntityInstanceUid,
                        fields(), true, isTranslationOn, translationLocale).execute();

        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            if (response != null && response.isSuccessful()) {
                TrackedEntityInstance trackedEntityInstance = response.body();

                trackedEntityInstanceHandler.handle(trackedEntityInstance);

                resourceHandler.handleResource(TRACKED_ENTITY_INSTANCE, serverDate);
                transaction.setSuccessful();
            }
        } finally {
            transaction.end();
        }

        return response;
    }

    private Fields<TrackedEntityInstance> fields() {
        return Fields.<TrackedEntityInstance>builder().fields(
                TrackedEntityInstance.uid, TrackedEntityInstance.created,
                TrackedEntityInstance.lastUpdated,
                TrackedEntityInstance.organisationUnit,
                TrackedEntityInstance.trackedEntity,
                TrackedEntityInstance.deleted,
                TrackedEntityInstance.relationships.with(
                        Relationship.trackedEntityInstanceA,
                        Relationship.trackedEntityInstanceB,
                        Relationship.relationship),
                TrackedEntityInstance.trackedEntityAttributeValues.with(
                        TrackedEntityAttributeValue.trackedEntityAttribute,
                        TrackedEntityAttributeValue.value,
                        TrackedEntityAttributeValue.created,
                        TrackedEntityAttributeValue.lastUpdated),
                TrackedEntityInstance.enrollment.with(
                        Enrollment.uid, Enrollment.created, Enrollment.lastUpdated,
                        Enrollment.coordinate,
                        Enrollment.dateOfEnrollment, Enrollment.dateOfIncident,
                        Enrollment.enrollmentStatus,
                        Enrollment.followUp, Enrollment.program, Enrollment.organisationUnit,
                        Enrollment.trackedEntityInstance,
                        Enrollment.deleted,
                        Enrollment.events.with(
                                Event.attributeCategoryOptions, Event.attributeOptionCombo,
                                Event.uid, Event.created, Event.lastUpdated, Event.completeDate,
                                Event.coordinates,
                                Event.dueDate, Event.enrollment, Event.eventDate, Event.eventStatus,
                                Event.organisationUnit, Event.program, Event.programStage,
                                Event.deleted,
                                Event.trackedEntityDataValues.with(
                                        TrackedEntityDataValue.created,
                                        TrackedEntityDataValue.lastUpdated,
                                        TrackedEntityDataValue.dataElement,
                                        TrackedEntityDataValue.providedElsewhere,
                                        TrackedEntityDataValue.storedBy,
                                        TrackedEntityDataValue.value
                                )
                        )
                )
        ).build();
    }
}
