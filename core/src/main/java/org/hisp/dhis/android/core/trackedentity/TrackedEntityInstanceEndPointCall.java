package org.hisp.dhis.android.core.trackedentity;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import android.util.Log;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.Date;

import retrofit2.Response;

import static org.hisp.dhis.android.core.resource.ResourceModel.Type.TRACKED_ENTITY_INSTANCE;

public class TrackedEntityInstanceEndPointCall implements
        Call<Response<Payload<TrackedEntityInstance>>> {

    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final DatabaseAdapter databaseAdapter;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final ResourceHandler resourceHandler;
    private final Date serverDate;
    private final String trackedEntityInstanceUid;

    private boolean isExecuted;

    public TrackedEntityInstanceEndPointCall(
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
            @NonNull ResourceHandler resourceHandler,
            @NonNull Date serverDate,
            @NonNull String trackedEntityInstanceUid) {
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
                        fields(), true).execute();

        if (response == null || !response.isSuccessful()) {
            return response;
        }

        Transaction transaction = databaseAdapter.beginNewTransaction();

        try {
            TrackedEntityInstance trackedEntityInstance = response.body();

            trackedEntityInstanceHandler.handle(trackedEntityInstance);

            resourceHandler.handleResource(TRACKED_ENTITY_INSTANCE, serverDate);

            transaction.setSuccessful();

        } catch (SQLiteConstraintException sql) {
            // This catch is necessary to ignore events with bad foreign keys exception
            // More info: If the foreign key have the flag
            // DEFERRABLE INITIALLY DEFERRED this exception will be throw in transaction
            // .end()
            // And the rollback will be executed only when the database is closed.
            // It is a reported as unfixed bug: https://issuetracker.google
            // .com/issues/37001653
            Log.d(this.getClass().getSimpleName(), sql.getMessage());
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
                TrackedEntityInstance.coordinates,
                TrackedEntityInstance.featureType,
                TrackedEntityInstance.relationships.with(
                        Relationship.trackedEntityInstanceA,
                        Relationship.trackedEntityInstanceB,
                        Relationship.displayName),
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
                        ),
                        Enrollment.notes.with(Note.allFields)
                )
        ).build();
    }
}
