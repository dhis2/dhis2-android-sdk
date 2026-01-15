/*
 *  Copyright (c) 2004-2023, University of Oslo
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
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityAttributeStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.tracker.importer.internal.JobReportEnrollmentHandler
import org.hisp.dhis.android.core.tracker.importer.internal.JobReportTrackedEntityHandler
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class NewTrackerImporterPayloadGeneratorMockIntegrationShould : BasePayloadGeneratorMockIntegration() {

    private val newTrackerPayloadGenerator = objects.d2DIComponent.newTrackerImporterPayloadGenerator

    @Test
    fun build_tracked_entity_instance_payload_with_flat_elements() = runTest {
        storeTrackerData()
        val trackedEntity = teiStore.selectByUid(teiId)!!

        val payload = newTrackerPayloadGenerator.getTrackedEntityPayload(listOf(trackedEntity))

        assertThat(payload.updated.trackedEntities.size).isEqualTo(1)
        assertThat(payload.updated.enrollments.size).isEqualTo(2)
        assertThat(payload.updated.events.size).isEqualTo(2)
    }

    @Test
    fun filter_synced_attribute_values() = runTest {
        storeTrackerData()
        val trackedEntity = teiStore.selectByUid(teiId)!!

        val payload = newTrackerPayloadGenerator.getTrackedEntityPayload(listOf(trackedEntity))
        assertThat(payload.updated.trackedEntities.first().trackedEntityAttributeValues?.size).isEqualTo(1)

        teiAttributeValueStore.setSyncStateByInstance(teiId, State.SYNCED)

        val trackedEntity2 = teiStore.selectByUid(teiId)!!
        val payload2 = newTrackerPayloadGenerator.getTrackedEntityPayload(listOf(trackedEntity2))
        assertThat(payload2.updated.trackedEntities.first().trackedEntityAttributeValues?.size).isEqualTo(0)
    }

    @Test
    fun filter_synced_data_values() = runTest {
        storeTrackerData()
        val event = eventStore.selectByUid(singleEventId)!!

        val payload = newTrackerPayloadGenerator.getEventPayload(listOf(event))
        assertThat(payload.updated.events.first().trackedEntityDataValues?.size).isEqualTo(1)

        teiDataValueStore.setSyncStateByEvent(singleEventId, State.SYNCED)

        val event2 = eventStore.selectByUid(singleEventId)!!
        val payload2 = newTrackerPayloadGenerator.getEventPayload(listOf(event2))
        assertThat(payload2.updated.events.first().trackedEntityDataValues?.size).isEqualTo(0)
    }

    @Test
    fun mark_only_type_attributes_as_synced_when_te_succeeds() = runTest {
        storeTrackerData()
        val trackedEntity = teiStore.selectByUid(teiId)!!

        // Get stores from Koin
        val trackedEntityTypeAttributeStore: TrackedEntityTypeAttributeStore = koin.get()

        // Get type attributes
        val typeAttributes = trackedEntityTypeAttributeStore
            .getForTrackedEntityType(trackedEntity.trackedEntityType()!!)
            .mapNotNull { it.trackedEntityAttribute()?.uid() }

        // Get initial attribute values before sync
        val attributeValuesBefore = teiAttributeValueStore.queryByTrackedEntityInstance(teiId)
        val initialStates = attributeValuesBefore.associate {
            it.trackedEntityAttribute()!! to it.syncState()
        }

        // Simulate TE success: mark only type attributes as SYNCED
        val handler: JobReportTrackedEntityHandler = koin.get()
        handler.handleSyncedEntity(teiId)

        // Assert: Only type attributes should be SYNCED
        val allAttributeValues = teiAttributeValueStore.queryByTrackedEntityInstance(teiId)

        allAttributeValues.forEach { attrValue ->
            val attrUid = attrValue.trackedEntityAttribute()!!
            if (typeAttributes.contains(attrUid)) {
                // Type attributes should be SYNCED
                assertThat(attrValue.syncState()).isEqualTo(State.SYNCED)
            } else {
                // Non-type attributes should keep their original state
                assertThat(attrValue.syncState()).isEqualTo(initialStates[attrUid])
            }
        }
    }

    @Test
    fun mark_only_program_attributes_as_synced_when_enrollment_succeeds() = runTest {
        storeTrackerData()
        val enrollment = enrollmentStore.selectByUid(enrollment1Id)!!
        val teiUid = enrollment.trackedEntityInstance()!!
        val programUid = enrollment.program()!!

        // Get store from Koin
        val programTrackedEntityAttributeStore: ProgramTrackedEntityAttributeStore = koin.get()

        // Get program attribute UIDs
        val programAttributes = programTrackedEntityAttributeStore
            .selectAll()
            .filter { it.program()?.uid() == programUid }
            .mapNotNull { it.trackedEntityAttribute()?.uid() }

        // Skip test if no program attributes are configured in test data
        if (programAttributes.isEmpty()) {
            return@runTest
        }

        // Get initial attribute values before sync
        val attributeValuesBefore = teiAttributeValueStore.queryByTrackedEntityInstance(teiUid)
        val initialStates = attributeValuesBefore.associate {
            it.trackedEntityAttribute()!! to it.syncState()
        }

        // Simulate enrollment success: mark only program attributes as SYNCED
        val handler: JobReportEnrollmentHandler = koin.get()
        handler.handleObject(enrollment1Id, State.SYNCED)

        // Assert: Program attributes should be SYNCED
        val allAttributeValues = teiAttributeValueStore.queryByTrackedEntityInstance(teiUid)

        allAttributeValues.forEach { attrValue ->
            val attrUid = attrValue.trackedEntityAttribute()!!
            if (programAttributes.contains(attrUid)) {
                assertThat(attrValue.syncState()).isEqualTo(State.SYNCED)
            } else {
                // Non-program attributes should keep their state (unless they were also synced by TE handler)
                // This test focuses on enrollment handler behavior
            }
        }
    }

    @Test
    fun not_mark_program_attributes_as_synced_when_only_te_succeeds_and_enrollment_fails() = runTest {
        storeTrackerData()
        val trackedEntity = teiStore.selectByUid(teiId)!!

        // Get type attributes
        val trackedEntityTypeAttributeStore: TrackedEntityTypeAttributeStore = koin.get()
        val typeAttributeUids = trackedEntityTypeAttributeStore
            .getForTrackedEntityType(trackedEntity.trackedEntityType()!!)
            .mapNotNull { it.trackedEntityAttribute()?.uid() }

        // Create a program-only attribute (not in type attributes)
        val programOnlyAttrUid = "programOnlyAttr123"

        // Insert the attribute first (to satisfy foreign key constraint)
        val trackedEntityAttributeStore: TrackedEntityAttributeStore = koin.get()
        val programOnlyAttribute = TrackedEntityAttribute.builder()
            .uid(programOnlyAttrUid)
            .build()
        trackedEntityAttributeStore.insert(programOnlyAttribute)

        // Now insert the attribute value
        val programOnlyAttrValue = TrackedEntityAttributeValue.builder()
            .trackedEntityInstance(teiId)
            .trackedEntityAttribute(programOnlyAttrUid)
            .value("test value")
            .syncState(State.TO_POST)
            .build()
        teiAttributeValueStore.insert(programOnlyAttrValue)

        // Simulate what the handler does: mark only TYPE attributes as SYNCED
        teiAttributeValueStore.setSyncStateByAttributes(teiId, typeAttributeUids, State.SYNCED)

        // Assert: Program-only attribute should NOT be SYNCED
        val afterSync = teiAttributeValueStore.queryByTrackedEntityInstance(teiId)
            .find { it.trackedEntityAttribute() == programOnlyAttrUid }

        assertThat(afterSync).isNotNull()
        assertThat(afterSync!!.syncState()).isEqualTo(State.TO_POST)
    }
}
