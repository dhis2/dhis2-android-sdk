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

import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import java.text.ParseException
import java.util.*
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl.Companion.create
import org.hisp.dhis.android.core.event.EventCreateProjection
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.event.internal.EventStoreImpl
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.core.relationship.internal.*
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStoreImpl
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataStatePropagatorIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    private lateinit var propagator: DataStatePropagator
    private lateinit var trackedEntityInstanceStore: TrackedEntityInstanceStore
    private lateinit var enrollmentStore: EnrollmentStore
    private lateinit var eventStore: EventStore
    private lateinit var relationshipStore: RelationshipStore
    private lateinit var relationshipItemStore: RelationshipItemStore
    private lateinit var relationshipTypeStore: IdentifiableObjectStore<RelationshipType>
    private lateinit var programOwnerStore: ObjectWithoutUidStore<ProgramOwner>

    private val relationshipType = "WiH6923nMtb"

    @Before
    @Throws(IOException::class)
    fun setUp() {
        trackedEntityInstanceStore = TrackedEntityInstanceStoreImpl.create(d2.databaseAdapter())
        enrollmentStore = create(d2.databaseAdapter())
        eventStore = EventStoreImpl.create(d2.databaseAdapter())
        relationshipStore = RelationshipStoreImpl.create(d2.databaseAdapter())
        relationshipItemStore = RelationshipItemStoreImpl.create(d2.databaseAdapter())
        relationshipTypeStore = RelationshipTypeStore.create(d2.databaseAdapter())
        programOwnerStore = ProgramOwnerStore.create(d2.databaseAdapter())

        propagator = DataStatePropagatorImpl(
            trackedEntityInstanceStore, enrollmentStore, eventStore,
            relationshipStore, relationshipItemStore, relationshipTypeStore, programOwnerStore
        )
    }

    @Test
    @Throws(D2Error::class)
    fun set_parent_state_to_update_if_has_synced_state() {
        assertThatSetTeiToUpdateWhenEnrollmentPropagation(State.SYNCED)
        assertThatSetTeiToUpdateWhenEventPropagation(State.SYNCED)
        assertThatSetTeiToUpdateWhenTrackedEntityDataValuePropagation(State.SYNCED)
    }

    @Test
    @Throws(D2Error::class)
    fun set_parent_state_to_update_if_has_to_update_state() {
        assertThatSetTeiToUpdateWhenEnrollmentPropagation(State.TO_UPDATE)
        assertThatSetTeiToUpdateWhenEventPropagation(State.TO_UPDATE)
        assertThatSetTeiToUpdateWhenTrackedEntityDataValuePropagation(State.TO_UPDATE)
    }

    @Test
    @Throws(D2Error::class)
    fun set_parent_state_to_update_if_has_to_post_state() {
        assertThatSetTeiToUpdateWhenEnrollmentPropagation(State.TO_POST)
        assertThatSetTeiToUpdateWhenEventPropagation(State.TO_POST)
        assertThatSetTeiToUpdateWhenTrackedEntityDataValuePropagation(State.TO_POST)
    }

    @Test
    @Throws(D2Error::class)
    fun set_parent_state_to_update_if_has_to_update_synced_via_sms() {
        assertThatSetTeiToUpdateWhenEnrollmentPropagation(State.SYNCED_VIA_SMS)
        assertThatSetTeiToUpdateWhenEventPropagation(State.SYNCED_VIA_SMS)
        assertThatSetTeiToUpdateWhenTrackedEntityDataValuePropagation(State.SYNCED_VIA_SMS)
    }

    @Test
    @Throws(D2Error::class)
    fun set_parent_state_to_update_if_has_to_update_sent_via_sms() {
        assertThatSetTeiToUpdateWhenEnrollmentPropagation(State.SENT_VIA_SMS)
        assertThatSetTeiToUpdateWhenEventPropagation(State.SENT_VIA_SMS)
        assertThatSetTeiToUpdateWhenTrackedEntityDataValuePropagation(State.SENT_VIA_SMS)
    }

    @Test
    @Throws(D2Error::class)
    fun do_not_set_parent_state_to_update_if_has_error_state() {
        assertThatDoNotSetTeiToUpdateWhenEnrollmentPropagation(State.ERROR)
        assertThatDoNotSetTeiToUpdateWhenEventPropagation(State.ERROR)
        assertThatDoNotSetTeiToUpdateWhenTrackedEntityDataValuePropagation(State.ERROR)
    }

    @Test
    @Throws(D2Error::class)
    fun do_not_set_parent_state_to_update_if_has_warning_state() {
        assertThatDoNotSetTeiToUpdateWhenEnrollmentPropagation(State.WARNING)
        assertThatDoNotSetTeiToUpdateWhenEventPropagation(State.WARNING)
        assertThatDoNotSetTeiToUpdateWhenTrackedEntityDataValuePropagation(State.WARNING)
    }

    @Test
    @Throws(D2Error::class)
    fun do_not_fail_with_events_without_registration() {
        val eventUid = d2.eventModule().events().blockingAdd(sampleEventProjection(null))

        assertThat(eventStore.selectByUid(eventUid)!!.syncState()).isEqualTo(State.TO_POST)

        eventStore.delete(eventUid)
    }

    @Test
    @Throws(D2Error::class)
    fun reset_enrollment_and_event_states_if_uploading() {
        val teiUid = d2.trackedEntityModule().trackedEntityInstances().blockingAdd(sampleTEIProjection())
        val enrolmentUid1 = d2.enrollmentModule().enrollments().blockingAdd(sampleEnrollmentProjection(teiUid))
        val enrolmentUid2 = d2.enrollmentModule().enrollments().blockingAdd(sampleEnrollmentProjection(teiUid))
        val eventUid1 = d2.eventModule().events().blockingAdd(sampleEventProjection(enrolmentUid1))
        val eventUid2 = d2.eventModule().events().blockingAdd(sampleEventProjection(enrolmentUid1))
        val eventUid3 = d2.eventModule().events().blockingAdd(sampleEventProjection(enrolmentUid2))

        enrollmentStore.setSyncState(enrolmentUid1, State.UPLOADING)
        eventStore.setSyncState(eventUid1, State.UPLOADING)
        propagator.resetUploadingEnrollmentAndEventStates(teiUid)

        assertThat(enrollmentStore.getSyncState(enrolmentUid1)).isEqualTo(State.TO_UPDATE)
        assertThat(enrollmentStore.getSyncState(enrolmentUid2)).isEqualTo(State.TO_POST)

        assertThat(eventStore.getSyncState(eventUid1)).isEqualTo(State.TO_UPDATE)
        assertThat(eventStore.getSyncState(eventUid2)).isEqualTo(State.TO_POST)
        assertThat(eventStore.getSyncState(eventUid3)).isEqualTo(State.TO_POST)

        trackedEntityInstanceStore.delete(teiUid)
    }

    @Test
    @Throws(D2Error::class, ParseException::class)
    fun propagate_last_updated_if_previous_is_older() {
        val oldDate = BaseIdentifiableObject.DATE_FORMAT.parse("1990-09-20T08:36:46.552")
        val teiUid = createTEIWithLastUpdated(oldDate)

        propagator.propagateEnrollmentUpdate(Enrollment.builder().uid("uid").trackedEntityInstance(teiUid).build())

        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.lastUpdated()).isGreaterThan(oldDate)

        trackedEntityInstanceStore.delete(teiUid)
    }

    @Test
    @Throws(D2Error::class, ParseException::class)
    fun do_not_propagate_last_updated_if_previous_is_newer() {
        val newerDate = BaseIdentifiableObject.DATE_FORMAT.parse("2990-09-20T08:36:46.552")
        val teiUid = createTEIWithLastUpdated(newerDate)

        propagator.propagateEnrollmentUpdate(Enrollment.builder().uid("uid").trackedEntityInstance(teiUid).build())

        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.lastUpdated()).isEqualTo(newerDate)

        trackedEntityInstanceStore.delete(teiUid)
    }

    @Test
    @Throws(D2Error::class)
    fun propagate_last_updated_if_previous_is_null() {
        val teiUid = createTEIWithLastUpdated(null)
        d2.enrollmentModule().enrollments().blockingAdd(sampleEnrollmentProjection(teiUid))

        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.lastUpdated()).isNotNull()

        trackedEntityInstanceStore.delete(teiUid)
    }

    @Test
    @Throws(D2Error::class)
    fun propagate_tei_relationship_update() {
        listOf(true, false).forEach { bidirectional ->
            val originalRelationshipType = relationshipTypeStore.selectByUid(relationshipType)!!
            relationshipTypeStore.update(originalRelationshipType.toBuilder().bidirectional(bidirectional).build())

            val teiUid = createTEIWithState(State.SYNCED)
            val toTeiUid = createTEIWithState(State.SYNCED)

            val relationshipUid = d2.relationshipModule().relationships().blockingAdd(
                Relationship.builder()
                    .relationshipType(relationshipType)
                    .from(RelationshipHelper.teiItem(teiUid))
                    .to(RelationshipHelper.teiItem(toTeiUid))
                    .build()
            )

            assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.syncState()).isEqualTo(State.SYNCED)
            assertThat(
                trackedEntityInstanceStore.selectByUid(teiUid)!!.aggregatedSyncState()
            ).isEqualTo(State.TO_UPDATE)

            val toTeiState = if (bidirectional) State.TO_UPDATE else State.SYNCED
            assertThat(trackedEntityInstanceStore.selectByUid(toTeiUid)!!.aggregatedSyncState()).isEqualTo(toTeiState)

            trackedEntityInstanceStore.delete(teiUid)
            trackedEntityInstanceStore.delete(toTeiUid)
            relationshipStore.delete(relationshipUid)
            relationshipTypeStore.update(originalRelationshipType)
        }
    }

    @Test
    @Throws(D2Error::class)
    fun propagate_enrollment_relationship_update() {
        listOf(true, false).forEach { bidirectional ->
            val originalRelationshipType = relationshipTypeStore.selectByUid(relationshipType)!!
            relationshipTypeStore.update(originalRelationshipType.toBuilder().bidirectional(bidirectional).build())

            val teiUid = createTEIWithState(State.SYNCED)
            val enrollmentUid = createEnrollmentWithState(State.SYNCED, teiUid)
            val toTeiUid = createTEIWithState(State.SYNCED)

            val relationshipUid = d2.relationshipModule().relationships().blockingAdd(
                Relationship.builder()
                    .relationshipType(relationshipType)
                    .from(RelationshipHelper.enrollmentItem(enrollmentUid))
                    .to(RelationshipHelper.teiItem(toTeiUid))
                    .build()
            )

            assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.syncState()).isEqualTo(State.SYNCED)
            assertThat(
                trackedEntityInstanceStore.selectByUid(teiUid)!!.aggregatedSyncState()
            ).isEqualTo(State.TO_UPDATE)
            assertThat(enrollmentStore.selectByUid(enrollmentUid)!!.syncState()).isEqualTo(State.SYNCED)
            assertThat(enrollmentStore.selectByUid(enrollmentUid)!!.aggregatedSyncState()).isEqualTo(State.TO_UPDATE)

            val toTeiState = if (bidirectional) State.TO_UPDATE else State.SYNCED
            assertThat(trackedEntityInstanceStore.selectByUid(toTeiUid)!!.aggregatedSyncState()).isEqualTo(toTeiState)

            trackedEntityInstanceStore.delete(teiUid)
            trackedEntityInstanceStore.delete(toTeiUid)
            relationshipStore.delete(relationshipUid)
            relationshipTypeStore.update(originalRelationshipType)
        }
    }

    @Test
    @Throws(D2Error::class)
    fun propagate_event_relationship_update() {
        listOf(true, false).forEach { bidirectional ->
            val originalRelationshipType = relationshipTypeStore.selectByUid(relationshipType)!!
            relationshipTypeStore.update(originalRelationshipType.toBuilder().bidirectional(bidirectional).build())

            val teiUid = createTEIWithState(State.SYNCED)
            val enrollmentUid = createEnrollmentWithState(State.SYNCED, teiUid)
            val eventUid = createEventWithState(State.SYNCED, enrollmentUid)
            val toTeiUid = createTEIWithState(State.SYNCED)

            val relationshipUid = d2.relationshipModule().relationships().blockingAdd(
                Relationship.builder()
                    .relationshipType(relationshipType)
                    .from(RelationshipHelper.eventItem(eventUid))
                    .to(RelationshipHelper.teiItem(toTeiUid))
                    .build()
            )

            assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.syncState()).isEqualTo(State.SYNCED)
            assertThat(
                trackedEntityInstanceStore.selectByUid(teiUid)!!.aggregatedSyncState()
            ).isEqualTo(State.TO_UPDATE)
            assertThat(enrollmentStore.selectByUid(enrollmentUid)!!.syncState()).isEqualTo(State.SYNCED)
            assertThat(enrollmentStore.selectByUid(enrollmentUid)!!.aggregatedSyncState()).isEqualTo(State.TO_UPDATE)
            assertThat(eventStore.selectByUid(eventUid)!!.syncState()).isEqualTo(State.SYNCED)
            assertThat(eventStore.selectByUid(eventUid)!!.aggregatedSyncState()).isEqualTo(State.TO_UPDATE)

            val toTeiState = if (bidirectional) State.TO_UPDATE else State.SYNCED
            assertThat(trackedEntityInstanceStore.selectByUid(toTeiUid)!!.aggregatedSyncState()).isEqualTo(toTeiState)

            trackedEntityInstanceStore.delete(teiUid)
            trackedEntityInstanceStore.delete(toTeiUid)
            relationshipStore.delete(relationshipUid)
            relationshipTypeStore.update(originalRelationshipType)
        }
    }

    @Throws(D2Error::class)
    private fun assertThatSetTeiToUpdateWhenEnrollmentPropagation(state: State) {
        val teiUid = createTEIWithState(state)
        d2.enrollmentModule().enrollments().blockingAdd(sampleEnrollmentProjection(teiUid))

        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.syncState()).isEqualTo(state)
        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.aggregatedSyncState()).isEqualTo(State.TO_UPDATE)

        trackedEntityInstanceStore.delete(teiUid)
    }

    @Throws(D2Error::class)
    private fun assertThatDoNotSetTeiToUpdateWhenEnrollmentPropagation(state: State) {
        val teiUid = createTEIWithState(state)
        d2.enrollmentModule().enrollments().blockingAdd(sampleEnrollmentProjection(teiUid))

        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.syncState()).isEqualTo(state)
        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.aggregatedSyncState()).isEqualTo(state)

        trackedEntityInstanceStore.delete(teiUid)
    }

    @Throws(D2Error::class)
    private fun assertThatSetTeiToUpdateWhenEventPropagation(state: State) {
        val teiUid = createTEIWithState(state)
        val enrolmentUid = d2.enrollmentModule().enrollments().blockingAdd(sampleEnrollmentProjection(teiUid))

        enrollmentStore.setSyncState(enrolmentUid, state)
        enrollmentStore.setAggregatedSyncState(enrolmentUid, state)

        d2.eventModule().events().blockingAdd(sampleEventProjection(enrolmentUid))

        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.syncState()).isEqualTo(state)
        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.aggregatedSyncState()).isEqualTo(State.TO_UPDATE)
        assertThat(enrollmentStore.selectByUid(enrolmentUid)!!.syncState()).isEqualTo(state)
        assertThat(enrollmentStore.selectByUid(enrolmentUid)!!.aggregatedSyncState()).isEqualTo(State.TO_UPDATE)

        trackedEntityInstanceStore.delete(teiUid)
    }

    @Throws(D2Error::class)
    private fun assertThatDoNotSetTeiToUpdateWhenEventPropagation(state: State) {
        val teiUid = createTEIWithState(state)
        val enrolmentUid = createEnrollmentWithState(state, teiUid)

        d2.eventModule().events().blockingAdd(sampleEventProjection(enrolmentUid))

        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.syncState()).isEqualTo(state)
        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.aggregatedSyncState()).isEqualTo(state)
        assertThat(enrollmentStore.selectByUid(enrolmentUid)!!.syncState()).isEqualTo(state)
        assertThat(enrollmentStore.selectByUid(enrolmentUid)!!.aggregatedSyncState()).isEqualTo(state)

        trackedEntityInstanceStore.delete(teiUid)
    }

    @Throws(D2Error::class)
    private fun assertThatSetTeiToUpdateWhenTrackedEntityDataValuePropagation(state: State) {
        val teiUid = createTEIWithState(state)
        val enrolmentUid = createEnrollmentWithState(state, teiUid)
        val eventUid = createEventWithState(state, enrolmentUid)

        propagator.propagateTrackedEntityDataValueUpdate(TrackedEntityDataValue.builder().event(eventUid).build())

        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.syncState()).isEqualTo(state)
        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.aggregatedSyncState()).isEqualTo(State.TO_UPDATE)
        assertThat(enrollmentStore.selectByUid(enrolmentUid)!!.syncState()).isEqualTo(state)
        assertThat(enrollmentStore.selectByUid(enrolmentUid)!!.aggregatedSyncState()).isEqualTo(State.TO_UPDATE)
        assertThat(eventStore.selectByUid(eventUid)!!.syncState()).isIn(listOf(State.TO_POST, State.TO_UPDATE))

        trackedEntityInstanceStore.delete(teiUid)
    }

    @Throws(D2Error::class)
    private fun assertThatDoNotSetTeiToUpdateWhenTrackedEntityDataValuePropagation(state: State) {
        val teiUid = createTEIWithState(state)
        val enrolmentUid = createEnrollmentWithState(state, teiUid)
        val eventUid = createEventWithState(state, enrolmentUid)

        propagator.propagateTrackedEntityDataValueUpdate(TrackedEntityDataValue.builder().event(eventUid).build())

        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.syncState()).isEqualTo(state)
        assertThat(trackedEntityInstanceStore.selectByUid(teiUid)!!.aggregatedSyncState()).isEqualTo(state)
        assertThat(enrollmentStore.selectByUid(enrolmentUid)!!.syncState()).isEqualTo(state)
        assertThat(enrollmentStore.selectByUid(enrolmentUid)!!.aggregatedSyncState()).isEqualTo(state)
        assertThat(eventStore.selectByUid(eventUid)!!.syncState()).isIn(listOf(State.TO_POST, State.TO_UPDATE))

        trackedEntityInstanceStore.delete(teiUid)
    }

    @Throws(D2Error::class)
    private fun createTEIWithState(state: State): String {
        val teiUid = d2.trackedEntityModule().trackedEntityInstances().blockingAdd(sampleTEIProjection())

        trackedEntityInstanceStore.setSyncState(teiUid, state)
        trackedEntityInstanceStore.setAggregatedSyncState(teiUid, state)

        return teiUid
    }

    @Throws(D2Error::class)
    private fun createEnrollmentWithState(state: State, teiUid: String): String {
        val enrolmentUid = d2.enrollmentModule().enrollments().blockingAdd(sampleEnrollmentProjection(teiUid))

        enrollmentStore.setSyncState(enrolmentUid, state)
        enrollmentStore.setAggregatedSyncState(enrolmentUid, state)

        return enrolmentUid
    }

    @Throws(D2Error::class)
    private fun createEventWithState(state: State, enrolmentUid: String): String {
        val eventUid = d2.eventModule().events().blockingAdd(sampleEventProjection(enrolmentUid))

        eventStore.setSyncState(eventUid, state)
        eventStore.setAggregatedSyncState(eventUid, state)

        return eventUid
    }

    @Throws(D2Error::class)
    private fun createTEIWithLastUpdated(lastUpdated: Date?): String {
        val teiUid = d2.trackedEntityModule().trackedEntityInstances().blockingAdd(sampleTEIProjection())
        val existingTEI = trackedEntityInstanceStore.selectByUid(teiUid)

        trackedEntityInstanceStore.update(existingTEI!!.toBuilder().lastUpdated(lastUpdated).build())

        return teiUid
    }

    private fun sampleTEIProjection(): TrackedEntityInstanceCreateProjection {
        return TrackedEntityInstanceCreateProjection.create("DiszpKrYNg8", "nEenWmSyUEp")
    }

    private fun sampleEnrollmentProjection(teiUid: String): EnrollmentCreateProjection {
        return EnrollmentCreateProjection.create("DiszpKrYNg8", "lxAQ7Zs9VYR", teiUid)
    }

    private fun sampleEventProjection(enrollmentUid: String?): EventCreateProjection {
        return EventCreateProjection.create(
            enrollmentUid, "lxAQ7Zs9VYR", "dBwrot7S420",
            "DiszpKrYNg8", "bRowv6yZOF2"
        )
    }
}
