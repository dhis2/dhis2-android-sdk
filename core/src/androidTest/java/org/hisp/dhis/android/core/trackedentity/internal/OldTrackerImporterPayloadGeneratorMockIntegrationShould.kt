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
package org.hisp.dhis.android.core.trackedentity.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.AccessHelper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class OldTrackerImporterPayloadGeneratorMockIntegrationShould : BasePayloadGeneratorMockIntegration() {

    @Test
    fun build_tracked_entity_instance_payload_with_nested_elements() {
        storeTrackerData()
        val trackedEntityInstance = teiStore.selectByUid(teiId)!!

        val payload = oldTrackerPayloadGenerator.getTrackedEntityInstancePayload(listOf(trackedEntityInstance))

        assertThat(payload.trackedEntityInstances.size).isEqualTo(1)
        assertThat(payload.events.size).isEqualTo(0)
        assertThat(payload.relationships.size).isEqualTo(0)

        val payloadInstance = payload.trackedEntityInstances.first()

        assertThat(getEnrollments(payloadInstance).size).isEqualTo(2)
        for (enrollment in getEnrollments(payloadInstance)) {
            assertThat(getEvents(enrollment).size).isEqualTo(1)
            for (event in getEvents(enrollment)) {
                assertThat(event.trackedEntityDataValues()!!.size).isEqualTo(1)
            }
        }
    }

    @Test
    fun build_single_event_with_nested_elements() {
        storeTrackerData()
        val event = eventStore.selectByUid(singleEventId)!!

        val payload = oldTrackerPayloadGenerator.getEventPayload(listOf(event))

        assertThat(payload.trackedEntityInstances.size).isEqualTo(0)
        assertThat(payload.events.size).isEqualTo(1)
        assertThat(payload.relationships.size).isEqualTo(0)

        val payloadEvent = payload.events.first()

        assertThat(payloadEvent.trackedEntityDataValues()!!.size).isEqualTo(1)
    }

    @Test
    fun build_payload_from_tei_with_related_single_events() {
        storeTrackerData()
        storeRelationship(
            relationshipUid = "relationship1",
            from = RelationshipHelper.teiItem(teiId),
            to = RelationshipHelper.eventItem(singleEventId)
        )
        val instance = teiStore.selectByUid(teiId)!!

        val payload = oldTrackerPayloadGenerator.getTrackedEntityInstancePayload(listOf(instance))

        assertThat(payload.trackedEntityInstances.size).isEqualTo(1)
        assertThat(payload.events.size).isEqualTo(1)
        assertThat(payload.relationships.size).isEqualTo(1)

        assertThat(payload.events.first().uid()).isEqualTo(singleEventId)
    }

    @Test
    fun build_payload_from_tei_with_related_tracker_event() {
        val fromTeiUid = "fromTei"
        storeTrackerData()
        storeSimpleTrackedEntityInstance(fromTeiUid, State.TO_UPDATE)

        storeRelationship(
            relationshipUid = "relationship1",
            from = RelationshipHelper.teiItem(fromTeiUid),
            to = RelationshipHelper.eventItem(event1Id)
        )
        val instance = teiStore.selectByUid(fromTeiUid)!!

        val payload = oldTrackerPayloadGenerator.getTrackedEntityInstancePayload(listOf(instance))

        checkTeisInPayload(payload, fromTeiUid, teiId)
    }

    @Test
    fun build_payload_from_tei_with_related_enrollments() {
        val fromTeiUid = "fromTei"
        storeTrackerData()
        storeSimpleTrackedEntityInstance(fromTeiUid, State.TO_UPDATE)

        storeRelationship(
            relationshipUid = "relationship1",
            from = RelationshipHelper.teiItem(fromTeiUid),
            to = RelationshipHelper.enrollmentItem(enrollment1Id)
        )
        val instance = teiStore.selectByUid(fromTeiUid)!!

        val payload = oldTrackerPayloadGenerator.getTrackedEntityInstancePayload(listOf(instance))

        checkTeisInPayload(payload, fromTeiUid, teiId)
    }

    @Test
    fun build_payload_from_tei_with_related_trackedEntityInstances() {
        val fromTeiUid = "fromTei"
        storeTrackerData()
        storeSimpleTrackedEntityInstance(fromTeiUid, State.TO_UPDATE)

        storeRelationship(
            relationshipUid = "relationship1",
            from = RelationshipHelper.teiItem(fromTeiUid),
            to = RelationshipHelper.teiItem(teiId)
        )
        val instance = teiStore.selectByUid(fromTeiUid)!!

        val payload = oldTrackerPayloadGenerator.getTrackedEntityInstancePayload(listOf(instance))

        checkTeisInPayload(payload, fromTeiUid, teiId)
    }

    private fun checkTeisInPayload(payload: OldTrackerImporterPayload, fromTei: String, toTei: String) {
        assertThat(payload.trackedEntityInstances.size).isEqualTo(2)
        assertThat(payload.events.size).isEqualTo(0)
        assertThat(payload.relationships.size).isEqualTo(1)

        payload.trackedEntityInstances.forEach {
            when (it.uid()) {
                fromTei -> assertThat(TrackedEntityInstanceInternalAccessor.accessEnrollments(it)).isEmpty()
                toTei -> assertThat(TrackedEntityInstanceInternalAccessor.accessEnrollments(it)).isNotEmpty()
                else -> fail("Unexpected trackedEntityInstance uid: " + it.uid())
            }
        }
    }

    @Test
    fun build_payload_from_single_event_with_related_tei() {
        storeTrackerData()

        storeRelationship(
            relationshipUid = "relationship1",
            from = RelationshipHelper.eventItem(singleEventId),
            to = RelationshipHelper.teiItem(teiId)
        )
        val event = eventStore.selectByUid(singleEventId)!!

        val payload = oldTrackerPayloadGenerator.getEventPayload(listOf(event))

        assertThat(payload.trackedEntityInstances.size).isEqualTo(1)
        assertThat(payload.events.size).isEqualTo(1)
        assertThat(payload.relationships.size).isEqualTo(1)

        assertThat(payload.trackedEntityInstances.first().uid()).isEqualTo(teiId)
        assertThat(payload.events.first().uid()).isEqualTo(singleEventId)
    }

    @Test
    fun build_recursive_relationship() {
        storeTrackerData()

        storeRelationship(
            relationshipUid = "relationship1",
            from = RelationshipHelper.enrollmentItem(enrollment1Id),
            to = RelationshipHelper.enrollmentItem(enrollment1Id)
        )
        val instance = teiStore.selectByUid(teiId)!!

        val payload = oldTrackerPayloadGenerator.getTrackedEntityInstancePayload(listOf(instance))

        assertThat(payload.trackedEntityInstances.size).isEqualTo(1)
        assertThat(payload.events.size).isEqualTo(0)
        assertThat(payload.relationships.size).isEqualTo(1)
    }

    @Test
    fun build_payload_with_non_accessible_adta() {
        storeTrackerData()
        val previousTrackedEntityType = trackedEntityTypeStore.selectFirst()!!
        val previousProgram = programStore.selectFirst()!!

        val noWriteAccess = AccessHelper.createForDataWrite(false)
        trackedEntityTypeStore.update(previousTrackedEntityType.toBuilder().access(noWriteAccess).build())
        programStore.update(previousProgram.toBuilder().access(noWriteAccess).build())

        teiStore.setSyncState(teiId, State.SYNCED)
        enrollmentStore.setSyncState(listOf(enrollment1Id, enrollment2Id, enrollment3Id), State.SYNCED)

        val instance = teiStore.selectByUid(teiId)!!

        val payload = oldTrackerPayloadGenerator.getTrackedEntityInstancePayload(listOf(instance))

        assertThat(payload.trackedEntityInstances).isEmpty()
        assertThat(payload.events.size).isEqualTo(2)
        assertThat(payload.relationships).isEmpty()

        trackedEntityTypeStore.update(previousTrackedEntityType)
        programStore.update(previousProgram)
    }
}
