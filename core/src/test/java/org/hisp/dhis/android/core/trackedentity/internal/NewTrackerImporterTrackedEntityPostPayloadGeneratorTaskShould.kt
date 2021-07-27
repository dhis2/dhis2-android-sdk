/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.trackedentity.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.relationship.NewTrackerImporterRelationship
import org.hisp.dhis.android.core.relationship.NewTrackerImporterRelationshipItem
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class NewTrackerImporterTrackedEntityPostPayloadGeneratorTaskShould {

    private val uidGenerator = UidGeneratorImpl()

    @Test
    fun should_generate_granular_payload_for_enrollments() {
        val syncedTei = generateTEI(State.SYNCED)
        val updatedEnrollment = generateEnrollment(syncedTei.uid(), State.TO_UPDATE)
        val updatedEvent = generateEvent(updatedEnrollment.uid())

        val wrapper = getTaskFor(
            listOf(syncedTei),
            mapOf(updatedEnrollment.uid()!! to listOf(updatedEvent)),
            mapOf(syncedTei.uid()!! to listOf(updatedEnrollment)),
            emptyMap()
        ).generate()

        assertThat(wrapper.deleted.isEmpty()).isTrue()

        assertThat(wrapper.updated.trackedEntities).isEmpty()
        assertThat(wrapper.updated.enrollments).hasSize(1)
        assertThat(wrapper.updated.events).hasSize(1)
    }

    @Test
    fun should_generate_deleted_payload_for_event() {
        val syncedTei = generateTEI()
        val syncedEnrollment = generateEnrollment(syncedTei.uid())
        val deletedEvent = generateEvent(syncedEnrollment.uid(), true)

        val wrapper = getTaskFor(
            listOf(syncedTei),
            mapOf(syncedEnrollment.uid()!! to listOf(deletedEvent)),
            mapOf(syncedTei.uid()!! to listOf(syncedEnrollment)),
            emptyMap()
        ).generate()

        assertThat(wrapper.deleted.trackedEntities).isEmpty()
        assertThat(wrapper.deleted.enrollments).isEmpty()
        assertThat(wrapper.deleted.events).hasSize(1)

        assertThat(wrapper.updated.isEmpty()).isTrue()
    }

    @Test
    fun should_generate_granular_payload_for_relationships() {
        val syncedTei = generateTEI()
        val relationship = generateRelationship(
            NewTrackerImporterRelationshipItem.builder().trackedEntity(syncedTei.uid()).build()
        )

        val wrapper = getTaskFor(
            listOf(syncedTei),
            emptyMap(),
            emptyMap(),
            mapOf(syncedTei.uid() to listOf(relationship))
        ).generate()

        assertThat(wrapper.deleted.isEmpty()).isTrue()

        assertThat(wrapper.updated.trackedEntities).isEmpty()
        assertThat(wrapper.updated.enrollments).isEmpty()
        assertThat(wrapper.updated.events).isEmpty()
        assertThat(wrapper.updated.relationships).hasSize(1)
    }

    private fun generateTEI(
        synState: State = State.SYNCED,
        deleted: Boolean = false
    ): TrackedEntityInstance {
        return TrackedEntityInstance.builder()
            .uid(uidGenerator.generate())
            .syncState(synState)
            .aggregatedSyncState(State.TO_UPDATE)
            .deleted(deleted)
            .build()
    }

    private fun generateEnrollment(
        teiUid: String,
        synState: State = State.SYNCED,
        deleted: Boolean = false
    ): NewTrackerImporterEnrollment {
        return NewTrackerImporterEnrollment.builder()
            .uid(uidGenerator.generate())
            .trackedEntity(teiUid)
            .syncState(synState)
            .aggregatedSyncState(State.TO_UPDATE)
            .deleted(deleted)
            .build()
    }

    private fun generateEvent(
        enrollmentUid: String,
        deleted: Boolean = false
    ): NewTrackerImporterEvent {
        return NewTrackerImporterEvent.builder()
            .uid(uidGenerator.generate())
            .enrollment(enrollmentUid)
            .syncState(State.TO_UPDATE)
            .aggregatedSyncState(State.TO_UPDATE)
            .deleted(deleted)
            .build()
    }

    private fun generateRelationship(
        from: NewTrackerImporterRelationshipItem
    ): NewTrackerImporterRelationship {
        return NewTrackerImporterRelationship.builder()
            .uid(uidGenerator.generate())
            .from(from)
            .syncState(State.TO_UPDATE)
            .deleted(false)
            .build()
    }

    private fun getTaskFor(
        trackedEntities: List<TrackedEntityInstance>,
        eventMap: Map<String, List<NewTrackerImporterEvent>>,
        enrollmentMap: Map<String, List<NewTrackerImporterEnrollment>>,
        relationshipMap: Map<String, List<NewTrackerImporterRelationship>>
    ): NewTrackerImporterTrackedEntityPostPayloadGeneratorTask {
        return NewTrackerImporterTrackedEntityPostPayloadGeneratorTask(
            trackedEntities,
            mapOf(),
            eventMap,
            enrollmentMap,
            mapOf(),
            relationshipMap,
            listOf()
        )
    }
}
