package org.hisp.dhis.android.core.trackedentity;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import android.util.Log;

import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.List;

import retrofit2.Response;

public final class TeisEndPointCall extends SyncCall<Response<Payload<TrackedEntityInstance>>> {

    private final GenericCallData genericCallData;
    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final TeiQuery trackerQuery;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final ResourceHandler resourceHandler;

    private TeisEndPointCall(
            @NonNull GenericCallData genericCallData,
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull TeiQuery trackerQuery,
            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
            @NonNull ResourceHandler resourceHandler) {
        this.genericCallData = genericCallData;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.trackerQuery = trackerQuery;
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;
        this.resourceHandler = resourceHandler;
    }

    @Override
    public Response<Payload<TrackedEntityInstance>> call() throws Exception {
        super.setExecuted();

        String lastSyncedTEIs = resourceHandler.getLastUpdated(ResourceModel.Type.TRACKED_ENTITY_INSTANCE);

        Integer teisToRequest = Math.min(trackerQuery.getPageLimit(), trackerQuery.getPageSize());

        Response<Payload<TrackedEntityInstance>> response;

        response = trackedEntityInstanceService.getTEIs(
                Utils.joinCollectionWithSeparator(trackerQuery.getOrgUnits(), ";"),
                TrackedEntityInstance.lastUpdated.gt(lastSyncedTEIs), fields(),
                Boolean.TRUE, trackerQuery.getPage(), teisToRequest).execute();

        if (response.isSuccessful() && response.body().items() != null && !response.body().items().isEmpty()) {
            List<TrackedEntityInstance> trackedEntityInstances = response.body().items();
            persistTeis(trackedEntityInstances);

            resourceHandler.handleResource(ResourceModel.Type.TRACKED_ENTITY_INSTANCE, genericCallData.serverDate());
        }

        return response;
    }

    private void persistTeis(List<TrackedEntityInstance> trackedEntityInstances) {
        Transaction transaction = genericCallData.databaseAdapter().beginNewTransaction();
        try {
            trackedEntityInstanceHandler.handleMany(trackedEntityInstances);
            transaction.setSuccessful();
        } catch (SQLiteConstraintException sql) {
                /* This catch is necessary to ignore events with bad foreign keys exception
                More info: If the foreign key have the flag DEFERRABLE INITIALLY DEFERRED this exception will be
                throw in transaction.end()
                And the rollback will be executed only when the database is closed.
                It is a reported as unfixed bug: https://issuetracker.google.com/issues/37001653 */
            Log.d(this.getClass().getSimpleName(), sql.getMessage());
        } finally {
            transaction.end();
        }
    }

    private Fields<TrackedEntityInstance> fields() {
        return Fields.<TrackedEntityInstance>builder().fields(
                TrackedEntityInstance.uid, TrackedEntityInstance.created,
                TrackedEntityInstance.lastUpdated,
                TrackedEntityInstance.organisationUnit,
                TrackedEntityInstance.trackedEntityType,
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

    public static TeisEndPointCall create(GenericCallData genericCallData, TeiQuery teiQuery) {
        return new TeisEndPointCall(
                genericCallData,
                genericCallData.retrofit().create(TrackedEntityInstanceService.class),
                teiQuery,
                TrackedEntityInstanceHandler.create(genericCallData.databaseAdapter()),
                genericCallData.resourceHandler()
        );
    }
}
