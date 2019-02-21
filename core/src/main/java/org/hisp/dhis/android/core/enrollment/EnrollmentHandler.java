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

package org.hisp.dhis.android.core.enrollment;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.enrollment.note.NoteDHISVersionManager;
import org.hisp.dhis.android.core.enrollment.note.NoteUniquenessManager;
import org.hisp.dhis.android.core.event.Event;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class EnrollmentHandler extends IdentifiableSyncHandlerImpl<Enrollment> {
    private final NoteDHISVersionManager noteVersionManager;
    private final SyncHandlerWithTransformer<Event> eventHandler;
    private final SyncHandler<Note> noteHandler;
    private final NoteUniquenessManager noteUniquenessManager;
    private final OrphanCleaner<Enrollment, Event> eventOrphanCleaner;

    @Inject
    EnrollmentHandler(@NonNull NoteDHISVersionManager noteVersionManager,
                      @NonNull EnrollmentStore enrollmentStore,
                      @NonNull SyncHandlerWithTransformer<Event> eventHandler,
                      @NonNull OrphanCleaner<Enrollment, Event> eventOrphanCleaner,
                      @NonNull SyncHandler<Note> noteHandler,
                      @NonNull NoteUniquenessManager noteUniquenessManager) {
        super(enrollmentStore);
        this.noteVersionManager = noteVersionManager;
        this.eventHandler = eventHandler;
        this.noteHandler = noteHandler;
        this.noteUniquenessManager = noteUniquenessManager;
        this.eventOrphanCleaner = eventOrphanCleaner;
    }

    @Override
    protected void afterObjectHandled(Enrollment enrollment, HandleAction action) {
        if (action != HandleAction.Delete) {
            eventHandler.handleMany(enrollment.events(),
                    event -> event.toBuilder()
                            .state(State.SYNCED)
                            .build());

            Collection<Note> notes = new ArrayList<>();
            if (enrollment.notes() != null) {
                for (Note note : enrollment.notes()) {
                    notes.add(noteVersionManager.transform(enrollment, note));
                }
            }
            noteHandler.handleMany(noteUniquenessManager.buildUniqueCollection(notes, enrollment.uid()));
        }

        eventOrphanCleaner.deleteOrphan(enrollment, enrollment.events());
    }
}