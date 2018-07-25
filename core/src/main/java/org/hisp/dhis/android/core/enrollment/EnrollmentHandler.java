package org.hisp.dhis.android.core.enrollment;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.common.OrphanCleanerImpl;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.enrollment.note.NoteHandler;
import org.hisp.dhis.android.core.enrollment.note.NoteModel;
import org.hisp.dhis.android.core.enrollment.note.NoteModelBuilder;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventHandler;
import org.hisp.dhis.android.core.event.EventModel;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class EnrollmentHandler {
    private final EnrollmentStore enrollmentStore;
    private final EventHandler eventHandler;
    private final GenericHandler<Note, NoteModel> noteHandler;
    private final OrphanCleaner<Enrollment, Event> eventOrphanCleaner;

    EnrollmentHandler(@NonNull DatabaseAdapter databaseAdapter,
                      @NonNull EnrollmentStore enrollmentStore,
                      @NonNull EventHandler eventHandler,
                      @NonNull OrphanCleaner<Enrollment, Event> eventOrphanCleaner) {
        this.enrollmentStore = enrollmentStore;
        this.eventHandler = eventHandler;
        this.noteHandler = NoteHandler.create(databaseAdapter);
        this.eventOrphanCleaner = eventOrphanCleaner;
    }

    public void handle(@NonNull List<Enrollment> enrollments) {

        if (enrollments != null && !enrollments.isEmpty()) {
            int size = enrollments.size();

            for (int i = 0; i < size; i++) {
                Enrollment enrollment = enrollments.get(i);
                handle(enrollment);
            }
        }
    }

    private void handle(@NonNull Enrollment enrollment) {
        if (enrollment == null) {
            return;
        }

        if (isDeleted(enrollment)) {
            enrollmentStore.delete(enrollment.uid());
        } else {
            String latitude = null;
            String longitude = null;
            if (enrollment.coordinate() != null) {
                latitude = enrollment.coordinate().latitude().toString();
                longitude = enrollment.coordinate().longitude().toString();
            }

            int updatedRow = enrollmentStore.update(enrollment.uid(), enrollment.created(),
                    enrollment.lastUpdated(),
                    enrollment.createdAtClient(), enrollment.lastUpdatedAtClient(),
                    enrollment.organisationUnit(),
                    enrollment.program(), enrollment.enrollmentDate(),
                    enrollment.incidentDate(),
                    enrollment.followUp(), enrollment.enrollmentStatus(),
                    enrollment.trackedEntityInstance(),
                    latitude, longitude,
                    State.SYNCED, enrollment.uid());

            if (updatedRow <= 0) {
                enrollmentStore.insert(enrollment.uid(), enrollment.created(),
                        enrollment.lastUpdated(),
                        enrollment.createdAtClient(), enrollment.lastUpdatedAtClient(),
                        enrollment.organisationUnit(), enrollment.program(),
                        enrollment.enrollmentDate(),
                        enrollment.incidentDate(), enrollment.followUp(),
                        enrollment.enrollmentStatus(),
                        enrollment.trackedEntityInstance(), latitude, longitude,
                        State.SYNCED);
            }

            eventHandler.handleMany(enrollment.events());
            noteHandler.handleMany(enrollment.notes(), new NoteModelBuilder(enrollment));
        }
        eventOrphanCleaner.deleteOrphan(enrollment, enrollment.events());
    }

    public static EnrollmentHandler create(DatabaseAdapter databaseAdapter) {
        return new EnrollmentHandler(
                databaseAdapter,
                new EnrollmentStoreImpl(databaseAdapter),
                EventHandler.create(databaseAdapter),
                new OrphanCleanerImpl<Enrollment, Event>(EventModel.TABLE,
                        EventModel.Columns.ENROLLMENT, databaseAdapter)
        );
    }
}