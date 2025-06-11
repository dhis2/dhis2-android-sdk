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

import com.google.common.collect.Lists
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.data.database.ObjectWithoutUidStoreAbstractIntegrationShould
import org.hisp.dhis.android.core.data.dataelement.DataElementSamples
import org.hisp.dhis.android.core.data.program.ProgramStageDataElementSamples
import org.hisp.dhis.android.core.data.program.ProgramStageSamples
import org.hisp.dhis.android.core.data.trackedentity.EventSamples
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityDataValueSamples
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.event.internal.EventStoreImpl
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramStageDataElement
import org.hisp.dhis.android.core.program.internal.ProgramStageDataElementStoreImpl
import org.hisp.dhis.android.core.program.internal.ProgramStageStoreImpl
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.utils.integration.mock.TestDatabaseAdapterFactory
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class TrackedEntityDataValueStoreIntegrationShould
    : ObjectWithoutUidStoreAbstractIntegrationShould<TrackedEntityDataValue>(
    TrackedEntityDataValueStoreImpl(TestDatabaseAdapterFactory.get()),
    TrackedEntityDataValueTableInfo.TABLE_INFO, TestDatabaseAdapterFactory.get()
) {

    override fun buildObject(): TrackedEntityDataValue {
        return TrackedEntityDataValueSamples.get()
    }

    @After
    override fun tearDown() = runTest {
        super.tearDown()
        TrackedEntityInstanceStoreImpl(TestDatabaseAdapterFactory.get()).delete()
        EnrollmentStoreImpl(TestDatabaseAdapterFactory.get()).delete()
        EventStoreImpl(TestDatabaseAdapterFactory.get()).delete()
        ProgramStageStoreImpl(TestDatabaseAdapterFactory.get()).delete()
        ProgramStageDataElementStoreImpl(TestDatabaseAdapterFactory.get()).delete()
    }

    override fun buildObjectToUpdate(): TrackedEntityDataValue {
        return TrackedEntityDataValueSamples.get().toBuilder()
            .value("value_2")
            .build()
    }

    @Test
    fun delete_by_event_and_not_in_data_elements() = runTest {
        (store as TrackedEntityDataValueStore).insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_1").build()
        )
        store.insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_2").build()
        )
        store.insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_2").dataElement("data_element_1").build()
        )
        store.insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_2").dataElement("data_element_2").build()
        )

        store.deleteByEventAndNotInDataElements("event_1", Lists.newArrayList("data_element_1"))
        assertThat(store.count()).isEqualTo(3)
        assertThat(store.queryTrackedEntityDataValuesByEventUid("event_1").size).isEqualTo(1)
        assertThat(store.queryTrackedEntityDataValuesByEventUid("event_2").size).isEqualTo(2)

        store.deleteByEventAndNotInDataElements("event_2", ArrayList())
        assertThat(store.selectAll().size).isEqualTo(1)
        assertThat(store.queryTrackedEntityDataValuesByEventUid("event_2").size).isEqualTo(0)
    }

    @Test
    fun select_data_values_by_event_uid() = runTest {
        (store as TrackedEntityDataValueStore).insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_1").build()
        )
        store.insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_2").build()
        )
        store.insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_2").dataElement("data_element_1").build()
        )

        val dataValueForEvent1 = store.queryTrackedEntityDataValuesByEventUid("event_1")
        assertThat(dataValueForEvent1.size).isEqualTo(2)
        assertThat(dataValueForEvent1.iterator().next().event()).isEqualTo("event_1")

        val dataValueForEvent2 = store.queryTrackedEntityDataValuesByEventUid("event_2")
        assertThat(dataValueForEvent2.size).isEqualTo(1)
        assertThat(dataValueForEvent2.iterator().next().event()).isEqualTo("event_2")
    }

    @Test
    fun select_single_events_data_values() = runTest {
        val eventStore: EventStore = EventStoreImpl(TestDatabaseAdapterFactory.get())
        eventStore.insert(
            EventSamples.get().toBuilder().uid("event_1").enrollment(null).syncState(State.TO_POST).build()
        )
        eventStore.insert(EventSamples.get().toBuilder().uid("event_2").syncState(State.TO_POST).build())
        assertThat(eventStore.count()).isEqualTo(2)

        (store as TrackedEntityDataValueStore).insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_1").build()
        )
        store.insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_2").build()
        )
        store.insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_2").dataElement("data_element_1").build()
        )
        assertThat(store.selectAll().size).isEqualTo(3)

        val singleEventsDataValues: Map<String, List<TrackedEntityDataValue?>> =
            store.querySingleEventsTrackedEntityDataValues()
        assertThat(singleEventsDataValues.size).isEqualTo(1)
        assertThat(singleEventsDataValues["event_1"]!!.size).isEqualTo(2)
        assertThat(singleEventsDataValues["event_1"]!!.iterator().next()!!.event()).isEqualTo("event_1")
        assertThat(singleEventsDataValues["event_2"]).isNull()
    }

    @Test
    fun select_tracker_data_values() = runTest {
        val trackedEntityInstanceStore: TrackedEntityInstanceStore =
            TrackedEntityInstanceStoreImpl(TestDatabaseAdapterFactory.get())
        val trackedEntityInstance = TrackedEntityInstance.builder().uid("tei_uid")
            .organisationUnit("organisation_unit_uid").trackedEntityType("tei_type").syncState(State.TO_POST).build()
        trackedEntityInstanceStore.insert(trackedEntityInstance)

        val enrollmentStore: EnrollmentStore = EnrollmentStoreImpl(TestDatabaseAdapterFactory.get())
        val enrollment = Enrollment.builder().uid("enrollment").organisationUnit("organisation_unit")
            .program("program").trackedEntityInstance("tei_uid")
            .aggregatedSyncState(State.TO_POST).syncState(State.TO_POST).build()
        enrollmentStore.insert(enrollment)

        val eventStore: EventStore = EventStoreImpl(TestDatabaseAdapterFactory.get())
        eventStore.insert(EventSamples.get().toBuilder().uid("event_1").syncState(State.TO_POST).build())
        eventStore.insert(
            EventSamples.get().toBuilder().uid("event_2").enrollment(null).syncState(State.TO_POST).build()
        )

        assertThat(eventStore.count()).isEqualTo(2)

        store.insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_1").build()
        )
        store.insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_2").build()
        )
        store.insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_2").dataElement("data_element_1").build()
        )
        assertThat(store.selectAll().size).isEqualTo(3)

        val trackerDataValues: Map<String, List<TrackedEntityDataValue?>> = (store as TrackedEntityDataValueStore)
            .queryTrackerTrackedEntityDataValues()
        assertThat(trackerDataValues.size).isEqualTo(1)
        assertThat(trackerDataValues["event_1"]!!.size).isEqualTo(2)
        assertThat(trackerDataValues["event_1"]!!.iterator().next()!!.event()).isEqualTo("event_1")
        assertThat(trackerDataValues["event_2"]).isNull()
    }

    @Test
    fun remove_unassigned_event_values() = runTest {
        val stage = ProgramStageSamples.getProgramStage().toBuilder().uid("stage_uid").build()
        val stageStore: IdentifiableObjectStore<ProgramStage> = ProgramStageStoreImpl(TestDatabaseAdapterFactory.get())
        stageStore.insert(stage)

        val event = EventSamples.get().toBuilder().uid("event_1").programStage(stage.uid()).build()
        val eventStore: EventStore = EventStoreImpl(TestDatabaseAdapterFactory.get())
        eventStore.insert(event)

        val dataElement1 = "data_element_1"
        val dataElement2 = "data_element_2"
        val psStore: IdentifiableObjectStore<ProgramStageDataElement> =
            ProgramStageDataElementStoreImpl(TestDatabaseAdapterFactory.get())
        psStore.insert(
            ProgramStageDataElementSamples.getProgramStageDataElement().toBuilder()
                .uid(dataElement1)
                .dataElement(DataElementSamples.getDataElement().toBuilder().uid(dataElement1).build())
                .programStage(ObjectWithUid.create(stage.uid()))
                .build()
        )

        (store as TrackedEntityDataValueStore).insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event(event.uid()).dataElement(dataElement1).build()
        )
        store.insert(
            TrackedEntityDataValueSamples.get()
                .toBuilder().event(event.uid()).dataElement(dataElement2).build()
        )
        assertThat(store.queryTrackedEntityDataValuesByEventUid(event.uid()).size).isEqualTo(2)

        store.removeUnassignedDataValuesByEvent(event.uid())
        val values = store.queryTrackedEntityDataValuesByEventUid(event.uid())
        assertThat(values.size).isEqualTo(1)
        assertThat(values[0].dataElement()).isEqualTo(dataElement1)
    }
}
