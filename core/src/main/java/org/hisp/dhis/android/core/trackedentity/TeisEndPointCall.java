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
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class TeisEndPointCall implements Call<Response<Payload<TrackedEntityInstance>>> {

    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final DatabaseAdapter databaseAdapter;
    private final TeiQuery trackerQuery;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final ResourceHandler resourceHandler;
    private final Date serverDate;

    private boolean isExecuted;

    public TeisEndPointCall(@NonNull TrackedEntityInstanceService trackedEntityInstanceService,
                            @NonNull DatabaseAdapter databaseAdapter, @NonNull TeiQuery trackerQuery,
                            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
                            @NonNull ResourceHandler resourceHandler,
                            @NonNull Date serverDate) {

        this.databaseAdapter = databaseAdapter;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.trackerQuery = trackerQuery;
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;
        this.resourceHandler = resourceHandler;
        this.serverDate = new Date(serverDate.getTime());
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<TrackedEntityInstance>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }

        String lastSyncedTEIs = resourceHandler.getLastUpdated(ResourceModel.Type.TRACKED_ENTITY_INSTANCE);

        Response<Payload<TrackedEntityInstance>> response;

        response = trackedEntityInstanceService.getTEIs(trackerQuery.getOrgUnit(),
                TrackedEntityInstance.lastUpdated.gt(lastSyncedTEIs), fields(),
                Boolean.TRUE, trackerQuery.getPage(), trackerQuery.getPageSize()).execute();

        if (response.isSuccessful() && response.body().items() != null) {
            List<TrackedEntityInstance> trackedEntityInstances = response.body().items();
            int size = trackedEntityInstances.size();

            if (trackerQuery.getPageLimit() > 0) {
                size =  trackerQuery.getPageLimit();
            }

            for (int i = 0; i < size; i++) {
                Transaction transaction = databaseAdapter.beginNewTransaction();
                TrackedEntityInstance trackedEntityInstance = trackedEntityInstances.get(i);
                try {
                    trackedEntityInstanceHandler.handle(trackedEntityInstance);
                    transaction.setSuccessful();
                } catch (SQLiteConstraintException sql) {
                    /*
                    This catch is necessary to ignore events with bad foreign keys exception
                    More info: If the foreign key have the flag
                    DEFERRABLE INITIALLY DEFERRED this exception will be throw in transaction
                    .end()
                    And the rollback will be executed only when the database is closed.
                    It is a reported as unfixed bug: https://issuetracker.google
                    .com/issues/37001653
                    */
                    Log.d(this.getClass().getSimpleName(), sql.getMessage());
                } finally {
                    transaction.end();
                }
            }
            resourceHandler.handleResource(ResourceModel.Type.TRACKED_ENTITY_INSTANCE, serverDate);
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
