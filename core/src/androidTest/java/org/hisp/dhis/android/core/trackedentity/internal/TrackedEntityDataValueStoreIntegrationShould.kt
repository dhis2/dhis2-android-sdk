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

package org.hisp.dhis.android.core.trackedentity.internal;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.ObjectWithoutUidStoreAbstractIntegrationShould;
import org.hisp.dhis.android.core.data.dataelement.DataElementSamples;
import org.hisp.dhis.android.core.data.program.ProgramStageDataElementSamples;
import org.hisp.dhis.android.core.data.program.ProgramStageSamples;
import org.hisp.dhis.android.core.data.trackedentity.EventSamples;
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityDataValueSamples;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.internal.EventStore;
import org.hisp.dhis.android.core.event.internal.EventStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.internal.ProgramStageDataElementStoreImpl;
import org.hisp.dhis.android.core.program.internal.ProgramStageStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.utils.integration.mock.TestDatabaseAdapterFactory;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class TrackedEntityDataValueStoreIntegrationShould
        extends ObjectWithoutUidStoreAbstractIntegrationShould<TrackedEntityDataValue> {

    protected TrackedEntityDataValueStore store;

    public TrackedEntityDataValueStoreIntegrationShould() {
        super(new TrackedEntityDataValueStoreImpl(TestDatabaseAdapterFactory.get()),
                TrackedEntityDataValueTableInfo.TABLE_INFO, TestDatabaseAdapterFactory.get());
        this.store = new TrackedEntityDataValueStoreImpl(TestDatabaseAdapterFactory.get());
    }

    @Override
    protected TrackedEntityDataValue buildObject() {
        return TrackedEntityDataValueSamples.get();
    }

    @After
    public void tearDown() {
        super.tearDown();
        new TrackedEntityInstanceStoreImpl(TestDatabaseAdapterFactory.get()).delete();
        new EnrollmentStoreImpl(TestDatabaseAdapterFactory.get()).delete();
        new EventStoreImpl(TestDatabaseAdapterFactory.get()).delete();
        new ProgramStageStoreImpl(TestDatabaseAdapterFactory.get()).delete();
        new ProgramStageDataElementStoreImpl(TestDatabaseAdapterFactory.get()).delete();
    }

    @Override
    protected TrackedEntityDataValue buildObjectToUpdate() {
        return TrackedEntityDataValueSamples.get().toBuilder()
                .value("value_2")
                .build();
    }

    @Test
    public void delete_by_event_and_not_in_data_elements() {
        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_1").build());
        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_2").build());
        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_2").dataElement("data_element_1").build());
        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_2").dataElement("data_element_2").build());

        store.deleteByEventAndNotInDataElements("event_1", Lists.newArrayList("data_element_1"));
        assertThat(store.selectAll().size()).isEqualTo(3);
        assertThat(store.queryTrackedEntityDataValuesByEventUid("event_1").size()).isEqualTo(1);
        assertThat(store.queryTrackedEntityDataValuesByEventUid("event_2").size()).isEqualTo(2);

        store.deleteByEventAndNotInDataElements("event_2", new ArrayList<>());
        assertThat(store.selectAll().size()).isEqualTo(1);
        assertThat(store.queryTrackedEntityDataValuesByEventUid("event_2").size()).isEqualTo(0);
    }

    @Test
    public void select_data_values_by_event_uid() {
        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_1").build());
        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_2").build());
        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_2").dataElement("data_element_1").build());

        List<TrackedEntityDataValue> dataValueForEvent1 = store.queryTrackedEntityDataValuesByEventUid("event_1");
        assertThat(dataValueForEvent1.size()).isEqualTo(2);
        assertThat(dataValueForEvent1.iterator().next().event()).isEqualTo("event_1");

        List<TrackedEntityDataValue> dataValueForEvent2 = store.queryTrackedEntityDataValuesByEventUid("event_2");
        assertThat(dataValueForEvent2.size()).isEqualTo(1);
        assertThat(dataValueForEvent2.iterator().next().event()).isEqualTo("event_2");
    }

    @Test
    public void select_single_events_data_values() {
        EventStore eventStore = new EventStoreImpl(TestDatabaseAdapterFactory.get());
        eventStore.insert(EventSamples.get().toBuilder().uid("event_1").enrollment(null).syncState(State.TO_POST).build());
        eventStore.insert(EventSamples.get().toBuilder().uid("event_2").syncState(State.TO_POST).build());
        assertThat(eventStore.count()).isEqualTo(2);

        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_1").build());
        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_2").build());
        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_2").dataElement("data_element_1").build());
        assertThat(store.selectAll().size()).isEqualTo(3);

        Map<String, List<TrackedEntityDataValue>> singleEventsDataValues =
                store.querySingleEventsTrackedEntityDataValues();
        assertThat(singleEventsDataValues.size()).isEqualTo(1);
        assertThat(singleEventsDataValues.get("event_1").size()).isEqualTo(2);
        assertThat(singleEventsDataValues.get("event_1").iterator().next().event()).isEqualTo("event_1");
        assertThat(singleEventsDataValues.get("event_2")).isNull();
    }

    @Test
    public void select_tracker_data_values() {
        TrackedEntityInstanceStore trackedEntityInstanceStore =
                new TrackedEntityInstanceStoreImpl(TestDatabaseAdapterFactory.get());
        TrackedEntityInstance trackedEntityInstance = TrackedEntityInstance.builder().uid("tei_uid")
                .organisationUnit("organisation_unit_uid").trackedEntityType("tei_type").syncState(State.TO_POST).build();
        trackedEntityInstanceStore.insert(trackedEntityInstance);

        EnrollmentStore enrollmentStore = new EnrollmentStoreImpl(TestDatabaseAdapterFactory.get());
        Enrollment enrollment = Enrollment.builder().uid("enrollment").organisationUnit("organisation_unit")
                .program("program").trackedEntityInstance("tei_uid")
                .aggregatedSyncState(State.TO_POST).syncState(State.TO_POST).build();
        enrollmentStore.insert(enrollment);

        EventStore eventStore = new EventStoreImpl(TestDatabaseAdapterFactory.get());
        eventStore.insert(EventSamples.get().toBuilder().uid("event_1").syncState(State.TO_POST).build());
        eventStore.insert(EventSamples.get().toBuilder().uid("event_2").enrollment(null).syncState(State.TO_POST).build());

        assertThat(eventStore.count()).isEqualTo(2);

        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_1").build());
        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_1").dataElement("data_element_2").build());
        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event("event_2").dataElement("data_element_1").build());
        assertThat(store.selectAll().size()).isEqualTo(3);

        Map<String, List<TrackedEntityDataValue>> trackerDataValues = store.queryTrackerTrackedEntityDataValues();
        assertThat(trackerDataValues.size()).isEqualTo(1);
        assertThat(trackerDataValues.get("event_1").size()).isEqualTo(2);
        assertThat(trackerDataValues.get("event_1").iterator().next().event()).isEqualTo("event_1");
        assertThat(trackerDataValues.get("event_2")).isNull();
    }

    @Test
    public void remove_unassigned_event_values() {
        ProgramStage stage = ProgramStageSamples.getProgramStage().toBuilder().uid("stage_uid").build();
        IdentifiableObjectStore<ProgramStage> stageStore = new ProgramStageStoreImpl(TestDatabaseAdapterFactory.get());
        stageStore.insert(stage);

        Event event = EventSamples.get().toBuilder().uid("event_1").programStage(stage.uid()).build();
        EventStore eventStore = new EventStoreImpl(TestDatabaseAdapterFactory.get());
        eventStore.insert(event);

        String dataElement1 = "data_element_1";
        String dataElement2 = "data_element_2";
        IdentifiableObjectStore<ProgramStageDataElement> psStore =
                new ProgramStageDataElementStoreImpl(TestDatabaseAdapterFactory.get());
        psStore.insert(ProgramStageDataElementSamples.getProgramStageDataElement().toBuilder()
                .uid(dataElement1)
                .dataElement(DataElementSamples.getDataElement().toBuilder().uid(dataElement1).build())
                .programStage(ObjectWithUid.create(stage.uid()))
                .build());

        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event(event.uid()).dataElement(dataElement1).build());
        store.insert(TrackedEntityDataValueSamples.get()
                .toBuilder().event(event.uid()).dataElement(dataElement2).build());
        assertThat(store.queryTrackedEntityDataValuesByEventUid(event.uid()).size()).isEqualTo(2);

        store.removeUnassignedDataValuesByEvent(event.uid());
        List<TrackedEntityDataValue> values = store.queryTrackedEntityDataValuesByEventUid(event.uid());
        assertThat(values.size()).isEqualTo(1);
        assertThat(values.get(0).dataElement()).isEqualTo(dataElement1);
    }
}