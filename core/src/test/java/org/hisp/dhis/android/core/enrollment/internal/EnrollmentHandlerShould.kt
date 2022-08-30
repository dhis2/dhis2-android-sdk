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
package org.hisp.dhis.android.core.enrollment.internal

import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandler
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerParams
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.internal.NoteDHISVersionManager
import org.hisp.dhis.android.core.note.internal.NoteUniquenessManager
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers

@RunWith(JUnit4::class)
class EnrollmentHandlerShould {
    private val enrollmentStore: EnrollmentStore = mock()
    private val eventHandler: IdentifiableDataHandler<Event> = mock()
    private val noteHandler: Handler<Note> = mock()
    private val noteUniquenessManager: NoteUniquenessManager = mock()
    private val enrollment: Enrollment = mock()
    private val event: Event = mock()
    private val note: Note = mock()
    private val noteVersionManager: NoteDHISVersionManager = mock()
    private val relationshipItemRelatives: RelationshipItemRelatives = mock()
    private val enrollmentBuilder: Enrollment.Builder = mock()
    private val relationshipVersionManager: RelationshipDHISVersionManager = mock()
    private val relationshipHandler: RelationshipHandler = mock()
    private val relationshipOrphanCleaner: OrphanCleaner<Enrollment, Relationship> = mock()
    private val eventCleaner: OrphanCleaner<Enrollment, Event> = mock()

    // object to test
    private lateinit var enrollmentHandler: EnrollmentHandler

    @Before
    fun setUp() {
        whenever(enrollment.uid()).doReturn("test_enrollment_uid")
        whenever(EnrollmentInternalAccessor.accessEvents(enrollment)).doReturn(listOf(event))
        whenever(enrollment.notes()).doReturn(listOf(note))
        whenever(note.storedDate()).doReturn("2017-12-20T15:08:27.882")
        whenever(enrollment.toBuilder()).doReturn(enrollmentBuilder)
        whenever(enrollmentBuilder.syncState(State.SYNCED)).doReturn(enrollmentBuilder)
        whenever(enrollmentBuilder.aggregatedSyncState(State.SYNCED)).doReturn(enrollmentBuilder)
        whenever(enrollmentBuilder.build()).doReturn(enrollment)
        whenever(enrollmentStore.selectUidsWhere(ArgumentMatchers.anyString())).doReturn(listOf())

        enrollmentHandler = EnrollmentHandler(
            relationshipVersionManager, relationshipHandler, noteVersionManager,
            enrollmentStore, eventHandler, eventCleaner, noteHandler, noteUniquenessManager,
            relationshipOrphanCleaner
        )
    }

    @Test
    fun do_nothing_when_passing_null_argument() {
        val params = IdentifiableDataHandlerParams(hasAllAttributes = false, overwrite = false, asRelationship = false)
        enrollmentHandler.handleMany(null, params, relationshipItemRelatives)

        // verify that store or event handler is never called
        verify(enrollmentStore, never()).deleteIfExists(any())
        verify(enrollmentStore, never()).updateOrInsert(any())
        verify(eventHandler, never()).handleMany(any(), any(), any())
        verify(eventCleaner, never()).deleteOrphan(any(), any())
        verify(noteHandler, never()).handleMany(any())
    }

    @Test
    fun invoke_only_delete_when_a_enrollment_is_set_as_deleted() {
        whenever(enrollment.deleted()).doReturn(true)

        val params = IdentifiableDataHandlerParams(hasAllAttributes = false, overwrite = false, asRelationship = false)
        enrollmentHandler.handleMany(listOf(enrollment), params, relationshipItemRelatives)

        // verify that enrollment store is only invoked with delete
        verify(enrollmentStore, times(1)).deleteIfExists(any())
        verify(enrollmentStore, never()).updateOrInsert(any())
        verify(relationshipHandler, times(1)).deleteLinkedRelationships(any())

        // event handler should not be invoked
        verify(eventHandler, never()).handleMany(any(), any(), any())
        verify(eventCleaner, never()).deleteOrphan(any(), any())
        verify(noteHandler, never()).handleMany(any())
    }

    @Test
    fun invoke_only_update_or_insert_when_handle_enrollment_is_valid() {
        whenever(enrollment.deleted()).doReturn(false)
        whenever(enrollmentStore.updateOrInsert(any())).doReturn(HandleAction.Update)

        val params = IdentifiableDataHandlerParams(hasAllAttributes = false, overwrite = false, asRelationship = false)
        enrollmentHandler.handleMany(listOf(enrollment), params, relationshipItemRelatives)

        // verify that enrollment store is only invoked with update
        verify(enrollmentStore, times(1)).updateOrInsert(any())
        verify(enrollmentStore, never()).deleteIfExists(any())
        verify(relationshipHandler, never()).deleteLinkedRelationships(any())

        // event handler should be invoked once
        verify(eventHandler, times(1)).handleMany(any(), any(), any())
        verify(eventCleaner, times(1)).deleteOrphan(any(), any())
        verify(noteHandler, times(1)).handleMany(any())
    }
}
