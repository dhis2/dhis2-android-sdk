package org.hisp.dhis.android.core.enrollment;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.DataOrphanCleanerImpl;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.enrollment.note.NoteHandler;
import org.hisp.dhis.android.core.enrollment.note.NoteStore;
import org.hisp.dhis.android.core.enrollment.note.NoteToStoreTransformer;
import org.hisp.dhis.android.core.enrollment.note.NoteUniquenessManager;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventHandler;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

import java.util.ArrayList;
import java.util.Collection;

public class EnrollmentHandler extends IdentifiableSyncHandlerImpl<Enrollment> {
    private final DHISVersionManager versionManager;
    private final SyncHandler<Event> eventHandler;
    private final SyncHandler<Note> noteHandler;
    private final ObjectWithoutUidStore<Note> noteStore;
    private final OrphanCleaner<Enrollment, Event> eventOrphanCleaner;

    EnrollmentHandler(@NonNull DHISVersionManager versionManager,
                      @NonNull EnrollmentStore enrollmentStore,
                      @NonNull SyncHandler<Event> eventHandler,
                      @NonNull OrphanCleaner<Enrollment, Event> eventOrphanCleaner,
                      @NonNull SyncHandler<Note> noteHandler,
                      @NonNull ObjectWithoutUidStore<Note> noteStore) {
        super(enrollmentStore);
        this.versionManager = versionManager;
        this.eventHandler = eventHandler;
        this.noteHandler = noteHandler;
        this.noteStore = noteStore;
        this.eventOrphanCleaner = eventOrphanCleaner;
    }

    @Override
    protected void afterObjectHandled(Enrollment enrollment, HandleAction action) {
        if (action != HandleAction.Delete) {
            eventHandler.handleMany(enrollment.events());

            Collection<Note> notes = new ArrayList<>();
            NoteToStoreTransformer transformer = new NoteToStoreTransformer(enrollment, versionManager);
            if (enrollment.notes() != null) {
                for (Note note : enrollment.notes()) {
                    notes.add(transformer.transform(note));
                }
            }
            noteHandler.handleMany(NoteUniquenessManager.buildUniqueCollection(notes, enrollment.uid(), noteStore));
        }

        eventOrphanCleaner.deleteOrphan(enrollment, enrollment.events());
    }

    public static EnrollmentHandler create(DatabaseAdapter databaseAdapter, DHISVersionManager versionManager) {
        return new EnrollmentHandler(
                versionManager,
                EnrollmentStoreImpl.create(databaseAdapter),
                EventHandler.create(databaseAdapter),
                new DataOrphanCleanerImpl<Enrollment, Event>(EventModel.TABLE, EventModel.Columns.ENROLLMENT,
                        EventModel.Columns.STATE, databaseAdapter),
                NoteHandler.create(databaseAdapter),
                NoteStore.create(databaseAdapter)
        );
    }
}