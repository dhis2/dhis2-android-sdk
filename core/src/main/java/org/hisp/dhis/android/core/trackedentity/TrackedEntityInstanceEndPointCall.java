package org.hisp.dhis.android.core.trackedentity;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import android.util.Log;

import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.relationship.Relationship;

import retrofit2.Response;
import retrofit2.Retrofit;

public class TrackedEntityInstanceEndPointCall extends SyncCall<TrackedEntityInstance> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final String trackedEntityInstanceUid;

    TrackedEntityInstanceEndPointCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
            @NonNull String trackedEntityInstanceUid) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;

        if (trackedEntityInstanceUid == null || trackedEntityInstanceUid.isEmpty()) {
            throw new IllegalArgumentException(
                    "trackedEntityInstanceUid is required to realize a request");
        }

        this.trackedEntityInstanceUid = trackedEntityInstanceUid;
    }

    @Override
    public Response<TrackedEntityInstance> call() throws Exception {
        super.setExecuted();

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
                TrackedEntityInstance.trackedEntityType,
                TrackedEntityInstance.coordinates,
                TrackedEntityInstance.featureType,
                TrackedEntityInstance.deleted,
                TrackedEntityInstance.relationships.with(Relationship.allFields),
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

    public static TrackedEntityInstanceEndPointCall create(DatabaseAdapter databaseAdapter,
                                                           Retrofit retrofit,
                                                           String trackedEntityInstanceUid) {
        return new TrackedEntityInstanceEndPointCall(
                databaseAdapter,
                retrofit,
                retrofit.create(TrackedEntityInstanceService.class),
                TrackedEntityInstanceHandler.create(databaseAdapter),
                trackedEntityInstanceUid
        );
    }
}
