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
package org.hisp.dhis.android.core.common.internal

import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemChildrenAppender
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import org.junit.Before
import org.junit.Test

class TrackerDataManagerShould {

    private val trackedEntityStore: TrackedEntityInstanceStore = mock()
    private val enrollmentStore: EnrollmentStore = mock()
    private val eventStore: EventStore = mock()
    private val relationshipStore: RelationshipStore = mock()
    private val relationshipChildrenAppender: RelationshipItemChildrenAppender = mock()
    private val dataStatePropagator: DataStatePropagator = mock()
    private val programOwnerStore: ObjectWithoutUidStore<ProgramOwner> = mock()

    private val trackedEntity: TrackedEntityInstance = mock()
    private val enrollment: Enrollment = mock()
    private val event: Event = mock()
    private val relationship: Relationship = mock()

    private lateinit var trackerDataManager: TrackerDataManager

    @Before
    fun setUp() {
        whenever(trackedEntity.uid()).doReturn("tei_uid")
        whenever(enrollment.uid()).doReturn("enrollment_uid")
        whenever(event.uid()).doReturn("event_uid")
        whenever(relationship.uid()).doReturn("rel_uid")

        whenever(enrollmentStore.selectWhere(any())).doReturn(listOf(enrollment))
        whenever(eventStore.selectWhere(any())).doReturn(listOf(event))
        whenever(relationshipStore.getRelationshipsByItem(any())).doReturn(listOf(relationship))

        trackerDataManager = TrackerDataManagerImpl(
            trackedEntityStore, enrollmentStore, eventStore, relationshipStore,
            relationshipChildrenAppender, dataStatePropagator, programOwnerStore
        )
    }

    @Test
    fun cascade_tracked_entity_deletion_when_existing() {
        whenever(trackedEntity.syncState()).doReturn(State.TO_UPDATE)
        whenever(enrollment.syncState()).doReturn(State.TO_UPDATE)
        whenever(event.syncState()).doReturn(State.TO_UPDATE)
        whenever(relationship.syncState()).doReturn(State.TO_UPDATE)

        trackerDataManager.deleteTrackedEntity(trackedEntity)

        verify(trackedEntityStore, times(1)).setDeleted(any())
        verify(trackedEntityStore, times(1)).setSyncState(any<String>(), any())
        verify(enrollmentStore, times(1)).setDeleted(any())
        verify(enrollmentStore, times(1)).setSyncState(any<String>(), any())
        verify(eventStore, times(1)).setDeleted(any())
        verify(eventStore, times(1)).setSyncState(any<String>(), any())
        verify(relationshipStore, times(3)).setDeleted(any())
        verify(relationshipStore, times(3)).setSyncState(any<String>(), any())
    }

    @Test
    fun cascade_tracked_entity_deletion_when_new() {
        whenever(trackedEntity.syncState()).doReturn(State.TO_POST)
        whenever(enrollment.syncState()).doReturn(State.TO_POST)
        whenever(event.syncState()).doReturn(State.TO_POST)
        whenever(relationship.syncState()).doReturn(State.TO_POST)

        trackerDataManager.deleteTrackedEntity(trackedEntity)

        verify(trackedEntityStore, times(1)).delete(any())
        verify(enrollmentStore, times(1)).delete(any())
        verify(eventStore, times(1)).delete(any())
        verify(relationshipStore, times(3)).delete(any())
    }

    @Test
    fun create_program_owner_entry_on_new_enrollment() {
        whenever(enrollment.program()).doReturn("program")
        whenever(enrollment.trackedEntityInstance()).doReturn("instance")
        whenever(enrollment.organisationUnit()).doReturn("orgunit")

        trackerDataManager.propagateEnrollmentUpdate(enrollment, HandleAction.Insert)
        verify(programOwnerStore, times(1)).selectWhere(any())
        verify(programOwnerStore, times(1)).insert(any<ProgramOwner>())

        trackerDataManager.propagateEnrollmentUpdate(enrollment, HandleAction.Update)
        verify(programOwnerStore, times(1)).selectWhere(any())
        verifyNoMoreInteractions(programOwnerStore)
    }
}
