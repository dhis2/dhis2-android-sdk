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

import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.data.relationship.RelationshipSamples
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityAttributeValueSamples
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityDataValueSamples
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityInstanceSamples
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyCleanerImpl
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyViolationStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemStore
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.junit.After
import org.junit.BeforeClass

open class BasePayloadGeneratorMockIntegration : BaseMockIntegrationTestMetadataEnqueable() {

    protected val teiId = "teiId"
    protected val enrollment1Id = "enrollment1Id"
    protected val enrollment2Id = "enrollment2Id"
    protected val enrollment3Id = "enrollment3Id"
    protected val event1Id = "event1Id"
    protected val event2Id = "event2Id"
    protected val event3Id = "event3Id"
    protected val singleEventId = "singleEventId"
    protected val unassignedDataElementId = "bx6fsa0t90x"

    @After
    @Throws(D2Error::class)
    suspend fun tearDown() {
        d2.wipeModule().wipeData()
    }

    protected suspend fun storeTrackerData() {
        val orgUnit = koin.get<OrganisationUnitStore>().selectFirst()!!
        val teiType = koin.get<TrackedEntityTypeStore>().selectFirst()!!
        val program = d2.programModule().programs().one().blockingGet()!!
        val programStage = koin.get<ProgramStageStore>().selectFirst()!!

        val dataValue1 = TrackedEntityDataValueSamples.get().toBuilder()
            .syncState(State.TO_UPDATE)
            .event(event1Id)
            .build()

        val event1 = Event.builder()
            .uid(event1Id)
            .enrollment(enrollment1Id)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .syncState(State.TO_UPDATE)
            .aggregatedSyncState(State.TO_UPDATE)
            .trackedEntityDataValues(listOf(dataValue1))
            .build()

        val enrollment1 = EnrollmentInternalAccessor.insertEvents(Enrollment.builder(), listOf(event1))
            .uid(enrollment1Id)
            .program(program.uid())
            .organisationUnit(orgUnit.uid())
            .syncState(State.TO_POST)
            .aggregatedSyncState(State.TO_POST)
            .trackedEntityInstance(teiId)
            .build()

        val dataValue2 = TrackedEntityDataValueSamples.get().toBuilder()
            .syncState(State.SYNCED_VIA_SMS)
            .event(event2Id)
            .build()

        val event2 = Event.builder()
            .uid(event2Id)
            .enrollment(enrollment2Id)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .syncState(State.SYNCED_VIA_SMS)
            .aggregatedSyncState(State.SYNCED_VIA_SMS)
            .trackedEntityDataValues(listOf(dataValue2))
            .build()

        val enrollment2 = EnrollmentInternalAccessor.insertEvents(Enrollment.builder(), listOf(event2))
            .uid(enrollment2Id)
            .program(program.uid())
            .organisationUnit(orgUnit.uid())
            .syncState(State.TO_POST)
            .aggregatedSyncState(State.TO_POST)
            .trackedEntityInstance(teiId)
            .build()

        val dataValue3 = TrackedEntityDataValueSamples.get().toBuilder()
            .syncState(State.TO_UPDATE)
            .event(event3Id)
            .build()

        val event3 = Event.builder()
            .uid(event3Id)
            .enrollment(enrollment3Id)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .syncState(State.ERROR)
            .aggregatedSyncState(State.ERROR)
            .trackedEntityDataValues(listOf(dataValue3))
            .build()

        val enrollment3 = EnrollmentInternalAccessor.insertEvents(Enrollment.builder(), listOf(event3))
            .uid(enrollment3Id)
            .program(program.uid())
            .organisationUnit(orgUnit.uid())
            .syncState(State.TO_POST)
            .aggregatedSyncState(State.SYNCED)
            .trackedEntityInstance(teiId)
            .build()

        val attributeValue = TrackedEntityAttributeValueSamples.get().toBuilder()
            .syncState(State.TO_UPDATE)
            .trackedEntityInstance(teiId)
            .build()

        val tei = TrackedEntityInstanceInternalAccessor.insertEnrollments(
            TrackedEntityInstance.builder(),
            listOf(enrollment1, enrollment2, enrollment3),
        )
            .uid(teiId)
            .trackedEntityType(teiType.uid())
            .organisationUnit(orgUnit.uid())
            .syncState(State.TO_POST)
            .aggregatedSyncState(State.TO_POST)
            .trackedEntityAttributeValues(listOf(attributeValue))
            .build()

        val singleEventDataValue = TrackedEntityDataValueSamples.get().toBuilder()
            .syncState(State.TO_UPDATE)
            .event(singleEventId)
            .build()

        val singleEvent = Event.builder()
            .uid(singleEventId)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .syncState(State.TO_POST)
            .aggregatedSyncState(State.TO_POST)
            .trackedEntityDataValues(listOf(singleEventDataValue))
            .build()

        teiStore.insert(tei)
        teiAttributeValueStore.insert(attributeValue)
        enrollmentStore.insert(enrollment1)
        enrollmentStore.insert(enrollment2)
        enrollmentStore.insert(enrollment3)
        eventStore.insert(event1)
        eventStore.insert(event2)
        eventStore.insert(event3)
        eventStore.insert(singleEvent)
        teiDataValueStore.insert(dataValue1)
        teiDataValueStore.insert(dataValue2)
        teiDataValueStore.insert(dataValue3)
        teiDataValueStore.insert(singleEventDataValue)
    }

    protected suspend fun storeSimpleTrackedEntityInstance(teiUid: String, state: State) {
        val orgUnit = koin.get<OrganisationUnitStore>().selectFirst()
        val teiType = koin.get<TrackedEntityTypeStore>().selectFirst()
        koin.get<TrackedEntityInstanceStore>().insert(
            TrackedEntityInstanceSamples.get().toBuilder()
                .uid(teiUid)
                .trackedEntityType(teiType!!.uid())
                .organisationUnit(orgUnit!!.uid())
                .syncState(state)
                .aggregatedSyncState(state)
                .build(),
        )
    }

    @Throws(D2Error::class)
    protected suspend fun storeRelationship(
        relationshipUid: String,
        from: String,
        to: String,
    ) {
        storeRelationship(relationshipUid, RelationshipHelper.teiItem(from), RelationshipHelper.teiItem(to))
    }

    @Throws(D2Error::class)
    protected suspend fun storeRelationship(
        relationshipUid: String,
        from: RelationshipItem,
        to: RelationshipItem,
    ) {
        val relationshipType = koin.get<RelationshipTypeStore>().selectFirst()
        val executor = D2CallExecutor.create(databaseAdapter)
        executor.executeD2CallTransactionally<Unit> {
            koin.get<RelationshipStore>().insert(
                RelationshipSamples.get230(relationshipUid, from, to).toBuilder()
                    .relationshipType(relationshipType!!.uid())
                    .syncState(State.TO_POST)
                    .build(),
            )
            koin.get<RelationshipItemStore>().insert(
                from.toBuilder()
                    .relationship(ObjectWithUid.create(relationshipUid))
                    .relationshipItemType(RelationshipConstraintType.FROM)
                    .build(),
            )
            koin.get<RelationshipItemStore>().insert(
                to.toBuilder()
                    .relationship(ObjectWithUid.create(relationshipUid))
                    .relationshipItemType(RelationshipConstraintType.TO)
                    .build(),
            )
            ForeignKeyCleanerImpl(databaseAdapter, koin.get<ForeignKeyViolationStore>())
                .cleanForeignKeyErrors()
        }
    }

    protected fun getEnrollments(trackedEntityInstance: TrackedEntityInstance): List<Enrollment> {
        return TrackedEntityInstanceInternalAccessor.accessEnrollments(trackedEntityInstance)
    }

    protected fun getEvents(enrollment: Enrollment): List<Event> {
        return EnrollmentInternalAccessor.accessEvents(enrollment)
    }

    protected companion object {
        internal lateinit var teiStore: TrackedEntityInstanceStore
        internal lateinit var teiDataValueStore: TrackedEntityDataValueStore
        internal lateinit var teiAttributeValueStore: TrackedEntityAttributeValueStore
        internal lateinit var eventStore: EventStore
        internal lateinit var enrollmentStore: EnrollmentStore
        internal lateinit var trackedEntityTypeStore: TrackedEntityTypeStore
        internal lateinit var programStore: ProgramStore

        @BeforeClass
        @JvmStatic
        @Throws(Exception::class)
        fun setUp() {
            setUpClass()
            teiStore = koin.get()
            teiDataValueStore = koin.get()
            teiAttributeValueStore = koin.get()
            eventStore = koin.get()
            enrollmentStore = koin.get()
            trackedEntityTypeStore = koin.get()
            programStore = koin.get()
        }
    }
}
