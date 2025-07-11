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
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class NewTrackerImporterPayloadGeneratorMockIntegrationShould : BasePayloadGeneratorMockIntegration() {

    private val newTrackerPayloadGenerator = objects.d2DIComponent.newTrackerImporterPayloadGenerator

    @Test
    fun build_tracked_entity_instance_payload_with_flat_elements() {
        storeTrackerData()
        val trackedEntity = teiStore.selectByUid(teiId)!!

        val payload = newTrackerPayloadGenerator.getTrackedEntityPayload(listOf(trackedEntity))

        assertThat(payload.updated.trackedEntities.size).isEqualTo(1)
        assertThat(payload.updated.enrollments.size).isEqualTo(2)
        assertThat(payload.updated.events.size).isEqualTo(2)
    }

    @Test
    fun filter_synced_attribute_values() {
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
    fun filter_synced_data_values() {
        storeTrackerData()
        val event = eventStore.selectByUid(singleEventId)!!

        val payload = newTrackerPayloadGenerator.getEventPayload(listOf(event))
        assertThat(payload.updated.events.first().trackedEntityDataValues?.size).isEqualTo(1)

        teiDataValueStore.setSyncStateByEvent(singleEventId, State.SYNCED)

        val event2 = eventStore.selectByUid(singleEventId)!!
        val payload2 = newTrackerPayloadGenerator.getEventPayload(listOf(event2))
        assertThat(payload2.updated.events.first().trackedEntityDataValues?.size).isEqualTo(0)
    }
}
