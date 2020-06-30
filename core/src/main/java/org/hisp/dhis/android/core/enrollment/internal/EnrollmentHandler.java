/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.enrollment.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandler;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerImpl;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.note.internal.NoteDHISVersionManager;
import org.hisp.dhis.android.core.note.internal.NoteUniquenessManager;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager;
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class EnrollmentHandler extends IdentifiableDataHandlerImpl<Enrollment> {
    private final NoteDHISVersionManager noteVersionManager;
    private final IdentifiableDataHandler<Event> eventHandler;
    private final Handler<Note> noteHandler;
    private final NoteUniquenessManager noteUniquenessManager;
    private final OrphanCleaner<Enrollment, Event> eventOrphanCleaner;
    private final OrphanCleaner<Enrollment, Relationship> relationshipOrphanCleaner;

    @Inject
    EnrollmentHandler(
            @NonNull RelationshipDHISVersionManager relationshipVersionManager,
            @NonNull RelationshipHandler relationshipHandler,
            @NonNull NoteDHISVersionManager noteVersionManager,
            @NonNull EnrollmentStore enrollmentStore,
            @NonNull IdentifiableDataHandler<Event> eventHandler,
            @NonNull OrphanCleaner<Enrollment, Event> eventOrphanCleaner,
            @NonNull Handler<Note> noteHandler,
            @NonNull NoteUniquenessManager noteUniquenessManager,
            @NonNull OrphanCleaner<Enrollment, Relationship> relationshipOrphanCleaner) {
        super(enrollmentStore, relationshipVersionManager, relationshipHandler);
        this.noteVersionManager = noteVersionManager;
        this.eventHandler = eventHandler;
        this.noteHandler = noteHandler;
        this.noteUniquenessManager = noteUniquenessManager;
        this.eventOrphanCleaner = eventOrphanCleaner;
        this.relationshipOrphanCleaner = relationshipOrphanCleaner;
    }

    @Override
    protected Enrollment addRelationshipState(Enrollment object) {
        return object.toBuilder().state(State.RELATIONSHIP).build();
    }

    @Override
    protected Enrollment addSyncedState(Enrollment object) {
        return object.toBuilder().state(State.SYNCED).build();
    }

    @Override
    protected void deleteOrphans(Enrollment object) {
        relationshipOrphanCleaner.deleteOrphan(object, EnrollmentInternalAccessor.accessRelationships(object));
    }

    @Override
    protected void afterObjectHandled(Enrollment enrollment, HandleAction action, Boolean overwrite) {
        if (action != HandleAction.Delete) {
            eventHandler.handleMany(EnrollmentInternalAccessor.accessEvents(enrollment),
                    event -> event.toBuilder()
                            .state(State.SYNCED)
                            .build(),
                    overwrite);

            Collection<Note> notes = new ArrayList<>();
            if (enrollment.notes() != null) {
                for (Note note : enrollment.notes()) {
                    notes.add(noteVersionManager.transform(Note.NoteType.ENROLLMENT_NOTE, enrollment.uid(), note));
                }
            }
            Set<Note> notesToSync = noteUniquenessManager.buildUniqueCollection(
                    notes, Note.NoteType.ENROLLMENT_NOTE, enrollment.uid());
            noteHandler.handleMany(notesToSync);

            List<Relationship> relationships = EnrollmentInternalAccessor.accessRelationships(enrollment);
            if (relationships != null) {
                handleRelationships(relationships);
            }
        }

        eventOrphanCleaner.deleteOrphan(enrollment, EnrollmentInternalAccessor.accessEvents(enrollment));
    }
}