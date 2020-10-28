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

package org.hisp.dhis.android.core.event.internal;

import android.util.Log;

import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerImpl;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventInternalAccessor;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.note.internal.NoteDHISVersionManager;
import org.hisp.dhis.android.core.note.internal.NoteUniquenessManager;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager;
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler;
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class EventHandler extends IdentifiableDataHandlerImpl<Event> {
    private final HandlerWithTransformer<TrackedEntityDataValue> trackedEntityDataValueHandler;
    private final Handler<Note> noteHandler;
    private final NoteDHISVersionManager noteVersionManager;
    private final NoteUniquenessManager noteUniquenessManager;
    private final OrphanCleaner<Event, Relationship> relationshipOrphanCleaner;

    @Inject
    EventHandler(
            RelationshipDHISVersionManager relationshipVersionManager,
            RelationshipHandler relationshipHandler,
            EventStore eventStore,
            HandlerWithTransformer<TrackedEntityDataValue> trackedEntityDataValueHandler,
            Handler<Note> noteHandler,
            NoteDHISVersionManager noteVersionManager,
            NoteUniquenessManager noteUniquenessManager,
            OrphanCleaner<Event, Relationship> relationshipOrphanCleaner) {
        super(eventStore, relationshipVersionManager, relationshipHandler);
        this.trackedEntityDataValueHandler = trackedEntityDataValueHandler;
        this.noteHandler = noteHandler;
        this.noteVersionManager = noteVersionManager;
        this.noteUniquenessManager = noteUniquenessManager;
        this.relationshipOrphanCleaner = relationshipOrphanCleaner;
    }

    @Override
    protected void afterObjectHandled(Event event, HandleAction action, Boolean overwrite,
                                      RelationshipItemRelatives relatives) {
        final String eventUid = event.uid();

        if (action == HandleAction.Delete) {
            Log.d(this.getClass().getSimpleName(), eventUid + " with no org. unit, invalid eventDate or deleted");
        } else {
            trackedEntityDataValueHandler.handleMany(event.trackedEntityDataValues(),
                    dataValue -> dataValue.toBuilder().event(eventUid).build());

            Collection<Note> notes = new ArrayList<>();
            if (event.notes() != null) {
                for (Note note : event.notes()) {
                    notes.add(noteVersionManager.transform(Note.NoteType.EVENT_NOTE, event.uid(), note));
                }
            }
            Set<Note> notesToSync = noteUniquenessManager.buildUniqueCollection(
                    notes, Note.NoteType.EVENT_NOTE, event.uid());
            noteHandler.handleMany(notesToSync);

            List<Relationship> relationships = EventInternalAccessor.accessRelationships(event);
            if (relationships != null && !relationships.isEmpty()) {
                handleRelationships(relationships, event, relatives);
            }
        }
    }

    @Override
    protected boolean deleteIfCondition(Event event) {
        boolean validEventDate = event.eventDate() != null ||
                event.status() == EventStatus.SCHEDULE ||
                event.status() == EventStatus.SKIPPED ||
                event.status() == EventStatus.OVERDUE;

        return !validEventDate || event.organisationUnit() == null;
    }

    @Override
    protected Event addRelationshipState(Event object) {
        return object.toBuilder().state(State.RELATIONSHIP).build();
    }

    @Override
    protected Event addSyncedState(Event object) {
        return object.toBuilder().state(State.SYNCED).build();
    }

    @Override
    protected void deleteOrphans(Event object) {
        relationshipOrphanCleaner.deleteOrphan(object, EventInternalAccessor.accessRelationships(object));
    }
}