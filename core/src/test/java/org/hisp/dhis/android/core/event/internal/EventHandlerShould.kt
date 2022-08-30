/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.event.internal

import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerParams
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.internal.NoteDHISVersionManager
import org.hisp.dhis.android.core.note.internal.NoteUniquenessManager
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EventHandlerShould {
    private val eventStore: EventStore = mock()
    private val trackedEntityDataValueHandler: TrackedEntityDataValueHandler = mock()
    private val trackedEntityDataValue: TrackedEntityDataValue = mock()
    private val noteHandler: Handler<Note> = mock()
    private val noteUniquenessManager: NoteUniquenessManager = mock()
    private val note: Note = mock()
    private val noteVersionManager: NoteDHISVersionManager = mock()
    private val relationshipVersionManager: RelationshipDHISVersionManager = mock()
    private val relationshipItemRelatives: RelationshipItemRelatives = mock()
    private val relationshipHandler: RelationshipHandler = mock()
    private val eventBuilder: Event.Builder = mock()
    private val relationshipOrphanCleaner: OrphanCleaner<Event, Relationship> = mock()
    private val event: Event = mock()

    // object to test
    private lateinit var eventHandler: EventHandler

    @Before
    fun setUp() {
        whenever(event.uid()).doReturn("test_event_uid")
        whenever(event.notes()).doReturn(listOf(note))
        whenever(event.organisationUnit()).doReturn("org_unit_uid")
        whenever(event.status()).doReturn(EventStatus.SCHEDULE)
        whenever(event.trackedEntityDataValues()).doReturn(listOf(trackedEntityDataValue))
        whenever(eventStore.updateOrInsert(any())).doReturn(HandleAction.Insert)
        whenever(event.toBuilder()).doReturn(eventBuilder)
        whenever(eventBuilder.syncState(State.SYNCED)).doReturn(eventBuilder)
        whenever(eventBuilder.aggregatedSyncState(State.SYNCED)).doReturn(eventBuilder)
        whenever(eventBuilder.build()).doReturn(event)

        eventHandler = EventHandler(
            relationshipVersionManager, relationshipHandler, eventStore,
            trackedEntityDataValueHandler, noteHandler, noteVersionManager, noteUniquenessManager,
            relationshipOrphanCleaner
        )
    }

    @Test
    fun do_nothing_when_passing_empty_list_argument() {
        val params = IdentifiableDataHandlerParams(hasAllAttributes = false, overwrite = false, asRelationship = false)
        eventHandler.handleMany(listOf(), params, relationshipItemRelatives)

        // verify that store is never invoked
        verify(eventStore, never()).deleteIfExists(any())
        verify(eventStore, never()).update(any())
        verify(eventStore, never()).insert(any<Event>())
        verify(noteHandler, never()).handleMany(any())
    }

    @Test
    fun invoke_only_delete_when_a_event_is_set_as_deleted() {
        whenever(event.deleted()).doReturn(true)

        val params = IdentifiableDataHandlerParams(hasAllAttributes = false, overwrite = false, asRelationship = false)
        eventHandler.handleMany(listOf(event), params, relationshipItemRelatives)

        // verify that delete is invoked once
        verify(eventStore, times(1)).deleteIfExists(event.uid())
        verify(relationshipHandler, times(1)).deleteLinkedRelationships(any())

        // verify that update and insert is never invoked
        verify(eventStore, never()).update(any())
        verify(eventStore, never()).insert(any<Event>())
        verify(noteHandler, never()).handleMany(any())

        // verify that data value handler is never invoked
        verify(trackedEntityDataValueHandler, never()).handleMany(any(), any())
    }

    @Test
    fun invoke_update_and_insert_when_handle_event_not_inserted() {
        whenever(eventStore.updateOrInsert(any())).doReturn(HandleAction.Insert)
        whenever(event.organisationUnit()).doReturn("org_unit_uid")
        whenever(event.status()).doReturn(EventStatus.SCHEDULE)

        val params = IdentifiableDataHandlerParams(hasAllAttributes = false, overwrite = false, asRelationship = false)
        eventHandler.handleMany(listOf(event), params, relationshipItemRelatives)

        // verify that update and insert is invoked, since we're updating before inserting
        verify(eventStore, times(1)).updateOrInsert(any())
        verify(trackedEntityDataValueHandler, times(1)).handleMany(any(), any())
        verify(noteHandler, times(1)).handleMany(any())

        // verify that delete is never invoked
        verify(eventStore, never()).deleteIfExists(any())
        verify(relationshipHandler, never()).deleteLinkedRelationships(any())
    }

    @Test
    fun delete_event_data_values_if_empty_list() {
        whenever(event.trackedEntityDataValues()).doReturn(emptyList())

        val params = IdentifiableDataHandlerParams(hasAllAttributes = false, overwrite = false, asRelationship = false)
        eventHandler.handleMany(listOf(event), params, relationshipItemRelatives)

        verify(trackedEntityDataValueHandler, times(1)).removeEventDataValues(any())
        verify(trackedEntityDataValueHandler, never()).handleMany(any(), any())
    }
}
