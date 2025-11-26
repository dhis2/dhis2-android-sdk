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

import com.google.common.truth.Truth
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.arch.helpers.UidGenerator
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.junit.Before
import java.util.Date

class TrackedEntityInstancePostCallRealIntegrationShould : BaseRealIntegrationTest() {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private lateinit var uidGenerator: UidGenerator

    private lateinit var trackedEntityInstanceStore: TrackedEntityInstanceStore
    private lateinit var enrollmentStore: EnrollmentStore
    private lateinit var eventStore: EventStore
    private lateinit var trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore
    private lateinit var trackedEntityDataValueStore: TrackedEntityDataValueStore
    private lateinit var orgUnitUid: String
    private lateinit var programUid: String
    private lateinit var programStageUid: String
    private lateinit var dataElementUid: String
    private lateinit var trackedEntityUid: String
    private lateinit var trackedEntityAttributeUid: String
    private lateinit var coordinates: String
    private lateinit var geometry: Geometry
    private lateinit var eventUid: String
    private lateinit var enrollmentUid: String
    private lateinit var trackedEntityInstanceUid: String

    private lateinit var event1Uid: String
    private lateinit var enrollment1Uid: String
    private lateinit var trackedEntityInstance1Uid: String

    private lateinit var categoryComboOptionUid: String

    @Before
    override fun setUp() {
        super.setUp()

        trackedEntityInstanceStore = koin.get()
        enrollmentStore = koin.get()
        eventStore = koin.get()
        trackedEntityAttributeValueStore = koin.get()
        trackedEntityDataValueStore = koin.get()

        uidGenerator = UidGeneratorImpl()
        orgUnitUid = "DiszpKrYNg8"
        programUid = "IpHINAT79UW"
        programStageUid = "A03MvHHogjR"
        dataElementUid = "a3kGcGDCuk6"
        trackedEntityUid = "nEenWmSyUEp"
        trackedEntityAttributeUid = "w75KJ2mc4zz"

        coordinates = "[9,9]"
        geometry = Geometry.builder().type(FeatureType.POINT).coordinates("[-11.96, 9.49]").build()

        categoryComboOptionUid = "HllvX50cXC0"
        eventUid = uidGenerator.generate()
        enrollmentUid = uidGenerator.generate()
        trackedEntityInstanceUid = uidGenerator.generate()

        event1Uid = uidGenerator.generate()
        enrollment1Uid = uidGenerator.generate()
        trackedEntityInstance1Uid = uidGenerator.generate()
    }

    /*
     * If you want run this test you need config the correct uids in the server side.
     * At this moment is necessary add into the "child programme" program the category combo : Implementing Partner
     * */
    // @Test
    @Throws(Exception::class)
    fun response_true_when_data_sync() = runTest {
        downloadMetadata()

        createDummyDataToPost(
            orgUnitUid, programUid, programStageUid, trackedEntityUid, coordinates, geometry, eventUid,
            enrollmentUid, trackedEntityInstanceUid, trackedEntityAttributeUid, dataElementUid,
        )

        createDummyDataToPost(
            orgUnitUid, programUid, programStageUid, trackedEntityUid, coordinates, geometry,
            event1Uid, enrollment1Uid, trackedEntityInstance1Uid, trackedEntityAttributeUid, dataElementUid,
        )

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()
    }

    // @Test
    @Throws(Exception::class)
    fun add_and_post_tei_using_repositories() {
        downloadMetadata()

        val childProgramUid = "IpHINAT79UW"

        // Organisation unit module -> get one organisation unit
        val organisationUnit = d2.organisationUnitModule().organisationUnits().one().blockingGet()

        // Program module -> get the program by its uid
        val program = d2.programModule().programs()
            .uid(childProgramUid)
            .blockingGet()

        // Tracked entity module -> add a new tracked entity instance
        val teiUid = d2.trackedEntityModule().trackedEntityInstances()
            .blockingAdd(
                TrackedEntityInstanceCreateProjection.builder()
                    .organisationUnit(organisationUnit?.uid())
                    .trackedEntityType(program?.trackedEntityType()?.uid())
                    .build(),
            )

        // Enrollment module -> enroll the tracked entity instance to the program
        d2.enrollmentModule().enrollments().blockingAdd(
            EnrollmentCreateProjection.builder()
                .organisationUnit(organisationUnit?.uid())
                .program(program?.uid())
                .trackedEntityInstance(teiUid)
                .build(),
        )

        // Program module -> get the program tracked entity attributes of the program
        /*List<ProgramTrackedEntityAttribute> attributes = d2.programModule()
                .programTrackedEntityAttributes()
                .byProgram().eq(program.uid())
                .blockingGet();

        // Iterate the program tracked entity attributes
        for (ProgramTrackedEntityAttribute at : attributes) {
            if (at.mandatory()) {
                // For each one, if mandatory: Tracked entity module -> set a tracked entity attribute value.
                d2.trackedEntityModule().trackedEntityAttributeValues()
                        .value(at.trackedEntityAttribute().uid(), teiUid)
                        .blockingSet(at.name() + " - value");
            }
        }*/
        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()
    }

    /*
     * If you want run this test you need config the correct uids in the server side.
     * At this moment is necessary add into the "child programme" program the category combo : Implementing Partner
     * */
    // @Test
    @Throws(Exception::class)
    fun pull_event_after_push_tracked_entity_instance_with_that_event() = runTest {
        downloadMetadata()

        createDummyDataToPost(
            orgUnitUid, programUid, programStageUid, trackedEntityUid, coordinates, geometry,
            eventUid, enrollmentUid, trackedEntityInstanceUid, trackedEntityAttributeUid, dataElementUid,
        )

        postTrackedEntityInstances()

        val pushedTrackedEntityInstance = getTrackedEntityInstanceFromDB(
            trackedEntityInstanceUid,
        )
        val pushedEnrollment = getEnrollmentByTrackedEntityInstanceFromDb(trackedEntityInstanceUid)
        val pushedEvent = getEventFromDb(eventUid)

        d2.wipeModule().wipeEverything()

        downloadMetadata()

        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(trackedEntityInstanceUid)
            .blockingDownload()

        val downloadedTrackedEntityInstance = getTrackedEntityInstanceFromDB(
            trackedEntityInstanceUid,
        )
        val downloadedEnrollment = getEnrollmentByTrackedEntityInstanceFromDb(trackedEntityInstanceUid)
        val downloadedEvent = getEventFromDb(eventUid)

        assertPushAndDownloadTrackedEntityInstances(
            pushedTrackedEntityInstance,
            pushedEnrollment,
            pushedEvent,
            downloadedTrackedEntityInstance,
            downloadedEnrollment,
            downloadedEvent,
        )
    }

    // @Test
    @Throws(Exception::class)
    fun post_a_tei() = runTest {
        downloadMetadata()
        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(4).limitByOrgunit(true).blockingDownload()

        val tei = trackedEntityInstanceStore.selectFirst()

        val geometry = Geometry.builder().type(FeatureType.POINT).coordinates("[98.54, 4.65]").build()

        val newUid = uidGenerator.generate()

        insertATei(newUid, tei!!, geometry)

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        d2.wipeModule().wipeEverything()
        downloadMetadata()

        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(newUid).blockingDownload()

        val response = d2.trackedEntityModule().trackedEntityInstances().byUid().eq(newUid).blockingGet()

        val updatedTei = response[0]

        Truth.assertThat(updatedTei.geometry()).isEqualTo(geometry)
    }

    // @Test
    @Throws(Exception::class)
    fun post_more_than_one_tei() = runTest {
        downloadMetadata()
        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(4).limitByOrgunit(true).blockingDownload()

        val tei = trackedEntityInstanceStore.selectFirst()!!

        val newUid1 = uidGenerator.generate()
        val newUid2 = uidGenerator.generate()

        insertATei(newUid1, tei, tei.geometry()!!)
        insertATei(newUid2, tei, tei.geometry()!!)

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        d2.wipeModule().wipeEverything()
        downloadMetadata()

        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(newUid1).blockingDownload()

        val teiList = d2.trackedEntityModule().trackedEntityInstances().byUid().eq(newUid1).blockingGet()

        Truth.assertThat(teiList.size == 1).isTrue()
    }

    // @Test
    @Throws(Exception::class)
    fun post_teis_filtering_what_to_post() = runTest {
        downloadMetadata()
        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(4).limitByOrgunit(true).blockingDownload()

        val tei = trackedEntityInstanceStore.selectFirst()!!

        val newUid1 = uidGenerator.generate()
        val newUid2 = uidGenerator.generate()

        insertATei(newUid1, tei, tei.geometry()!!)
        insertATei(newUid2, tei, tei.geometry()!!)

        d2.trackedEntityModule().trackedEntityInstances().byUid().eq(newUid1).blockingUpload()

        d2.wipeModule().wipeEverything()
        downloadMetadata()

        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(newUid1).blockingDownload()

        val teiList = d2.trackedEntityModule().trackedEntityInstances().byUid().eq(newUid1).blockingGet()

        Truth.assertThat(teiList.size == 1).isTrue()

        var teiDownloadedSuccessfully = true
        try {
            d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(newUid2).blockingDownload()
        } catch (e: Exception) {
            teiDownloadedSuccessfully = false
        }
        Truth.assertThat(teiDownloadedSuccessfully).isFalse()
    }

    /* Set Dhis2 server to 2.30 or up*/
    // @Test
    @Throws(Exception::class)
    fun post_one_tei_and_delete_it() = runTest {
        downloadMetadata()
        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(1).limitByOrgunit(true).blockingDownload()

        val tei = trackedEntityInstanceStore.selectFirst()!!

        val geometry = Geometry.builder().type(FeatureType.POINT).coordinates("[98.54, 4.65]").build()

        val newUid = uidGenerator.generate()

        insertATei(newUid, tei, geometry)

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()
        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(newUid).blockingDownload()

        val response = d2.trackedEntityModule().trackedEntityInstances().byUid().eq(newUid).blockingGet()

        Truth.assertThat(response.size).isEqualTo(1)

        d2.trackedEntityModule().trackedEntityInstances().uid(newUid).blockingDelete()

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        val testObserver =
            d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(newUid).download().test()
        testObserver.awaitTerminalEvent()

        val e = testObserver.errors()[0] as D2Error

        Truth.assertThat(e.errorComponent()).isEqualTo(D2ErrorComponent.Server)
        Truth.assertThat(e.errorCode()).isEqualTo(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE)
    }

    // @Test
    @Throws(Exception::class)
    fun post_new_relationship_to_client_created_tei() = runTest {
        downloadMetadata()
        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(5).limitByOrgunit(true).blockingDownload()

        val teiA = trackedEntityInstanceStore.selectFirst()!!
        val relationshipType = d2.relationshipModule().relationshipTypes().blockingGet().iterator().next()
        val geometry = Geometry.builder().type(FeatureType.MULTI_POLYGON).coordinates("[98.54, 4.65]").build()

        // Create a TEI by copying an existing one
        val teiBUid = uidGenerator.generate()
        insertATei(teiBUid, teiA, geometry)

        trackedEntityInstanceStore.setSyncState(teiA.uid(), State.TO_POST)

        val newRelationship = RelationshipHelper.teiToTeiRelationship(
            teiA.uid(),
            teiBUid,
            relationshipType.uid(),
        )
        d2.relationshipModule().relationships().blockingAdd(newRelationship)

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        d2.wipeModule().wipeEverything()
        downloadMetadata()

        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(teiA.uid()).blockingDownload()
        val responseTeiA = d2.trackedEntityModule().trackedEntityInstances().byUid().eq(
            teiA.uid(),
        ).blockingGet()
        Truth.assertThat(responseTeiA.size == 1).isTrue()

        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(teiBUid).blockingDownload()
        val responseTeiB = d2.trackedEntityModule().trackedEntityInstances().byUid().eq(teiBUid).blockingGet()
        Truth.assertThat(responseTeiB.size == 1).isTrue()

        val relationships =
            d2.relationshipModule().relationships().getByItem(RelationshipHelper.teiItem(teiA.uid()), true, false)
        Truth.assertThat(relationships.size > 0).isTrue()

        var relationshipFound = false
        for (relationship in relationships) {
            if (relationshipType.uid() != relationship.relationshipType()) {
                break
            }
            val fromUid = getTEIUidFromRelationshipItem(relationship.from())
            val toUid = getTEIUidFromRelationshipItem(relationship.to())

            if (teiA.uid() == fromUid && teiBUid == toUid) {
                relationshipFound = true
            }
        }
        Truth.assertThat(relationshipFound).isTrue()
    }

    // @Test
    @Throws(Exception::class)
    fun create_tei_to_tei_relationship() = runTest {
        downloadMetadata()

        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(5).blockingDownload()
        val trackedEntityInstances = trackedEntityInstanceStore.selectAll()
        Truth.assertThat(trackedEntityInstances.size >= 5).isTrue()

        val t0 = trackedEntityInstances[0]
        val t1 = trackedEntityInstances[1]

        val relationshipType = d2.relationshipModule().relationshipTypes().blockingGet().iterator().next()

        d2.relationshipModule().relationships().blockingAdd(
            RelationshipHelper.teiToTeiRelationship(
                t0.uid(),
                t1.uid(),
                relationshipType.uid(),
            ),
        )

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()
    }

    // @Test
    @Throws(Exception::class)
    fun create_and_delete_tei_to_tei_relationship() = runTest {
        downloadMetadata()

        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(10).blockingDownload()
        val trackedEntityInstances = trackedEntityInstanceStore.selectAll()

        Truth.assertThat(trackedEntityInstances.size == 10).isTrue()

        val t0 = trackedEntityInstances[0]
        val t1 = trackedEntityInstances[1]

        val relationshipModule = d2.relationshipModule()
        val typesRepository = relationshipModule.relationshipTypes()
        val relationshipsRepository = relationshipModule.relationships()

        val relationshipType = typesRepository.blockingGet().iterator().next()

        val newRelationship = RelationshipHelper.teiToTeiRelationship(
            t0.uid(),
            t1.uid(),
            relationshipType.uid(),
        )
        relationshipsRepository.blockingAdd(newRelationship)

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        relationshipsRepository.uid(newRelationship.uid()).blockingDelete()

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()
    }

    // @Test
    @Throws(Exception::class)
    fun post_a_tei_and_delete_one_event() = runTest {
        downloadMetadata()
        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq("LxMVYhJm3Jp").blockingDownload()

        val event = eventStore.selectFirst()!!
        val eventUid = event.uid()

        d2.eventModule().events().uid(eventUid).blockingDelete()

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        d2.wipeModule().wipeEverything()
        downloadMetadata()
        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq("LxMVYhJm3Jp").blockingDownload()

        var deleted = true
        for (eventToCheck in eventStore.selectAll()) {
            if (eventToCheck.uid() == eventUid) {
                deleted = false
            }
        }

        Truth.assertThat(deleted).isTrue()
    }

    private suspend fun insertATei(uid: String, tei: TrackedEntityInstance, geometry: Geometry) {
        val trackedEntityInstance = tei.toBuilder()
            .uid(uid)
            .geometry(geometry)
            .syncState(State.TO_POST)
            .aggregatedSyncState(State.TO_POST)
            .build()

        trackedEntityInstanceStore.insert(trackedEntityInstance)
    }

    private suspend fun createDummyDataToPost(
        orgUnitUid: String,
        programUid: String,
        programStageUid: String,
        trackedEntityUid: String,
        coordinates: String,
        geometry: Geometry,
        eventUid: String,
        enrollmentUid: String,
        trackedEntityInstanceUid: String,
        trackedEntityAttributeUid: String,
        dataElementUid: String,
    ) {
        val refDate = currentDateMinusTwoHoursTenMinutes

        val trackedEntityInstance = TrackedEntityInstance.builder()
            .uid(trackedEntityInstanceUid)
            .created(refDate)
            .lastUpdated(refDate)
            .organisationUnit(orgUnitUid)
            .trackedEntityType(trackedEntityUid)
            .geometry(geometry)
            .syncState(State.TO_POST)
            .aggregatedSyncState(State.TO_POST)
            .build()

        trackedEntityInstanceStore.insert(trackedEntityInstance)

        val enrollment = Enrollment.builder()
            .uid(enrollmentUid).created(refDate).lastUpdated(refDate).organisationUnit(orgUnitUid)
            .program(programUid).incidentDate(refDate).completedDate(refDate).enrollmentDate(refDate)
            .followUp(java.lang.Boolean.FALSE).status(EnrollmentStatus.ACTIVE)
            .trackedEntityInstance(trackedEntityInstanceUid)
            .geometry(Geometry.builder().type(FeatureType.POINT).coordinates("[10.33, 12.231]").build())
            .syncState(State.TO_POST).aggregatedSyncState(State.TO_POST).build()

        enrollmentStore.insert(enrollment)

        val event = Event.builder()
            .uid(eventUid).enrollment(enrollmentUid).created(refDate).lastUpdated(refDate)
            .status(EventStatus.ACTIVE).program(programUid)
            .geometry(Geometry.builder().type(FeatureType.POINT).coordinates("[12.21, 13.21]").build())
            .programStage(programStageUid).organisationUnit(orgUnitUid).eventDate(refDate).dueDate(refDate)
            .completedDate(refDate).syncState(State.TO_POST).attributeOptionCombo(categoryComboOptionUid)
            .build()

        eventStore.insert(event)

        val trackedEntityDataValue = TrackedEntityDataValue.builder()
            .event(eventUid)
            .created(refDate)
            .lastUpdated(refDate)
            .dataElement(dataElementUid)
            .storedBy("user_name")
            .value("12")
            .providedElsewhere(java.lang.Boolean.FALSE)
            .build()

        trackedEntityDataValueStore.insert(trackedEntityDataValue)

        val trackedEntityAttributeValue = TrackedEntityAttributeValue.builder()
            .value("new2").created(refDate).lastUpdated(refDate).trackedEntityAttribute(trackedEntityAttributeUid)
            .trackedEntityInstance(trackedEntityInstanceUid).build()

        trackedEntityAttributeValueStore.insert(trackedEntityAttributeValue)
    }

    private fun assertPushAndDownloadTrackedEntityInstances(
        pushedTrackedEntityInstance: TrackedEntityInstance,
        pushedEnrollment: Enrollment,
        pushedEvent: Event,
        downloadedTrackedEntityInstance: TrackedEntityInstance,
        downloadedEnrollment: Enrollment,
        downloadedEvent: Event,
    ) {
        Truth.assertThat(pushedTrackedEntityInstance.uid() == downloadedTrackedEntityInstance.uid()).isTrue()
        Truth.assertThat(pushedTrackedEntityInstance.uid() == downloadedTrackedEntityInstance.uid()).isTrue()
        Truth.assertThat(pushedEnrollment.uid() == downloadedEnrollment.uid()).isTrue()
        Truth.assertThat(pushedEvent.uid() == downloadedEvent.uid()).isTrue()
        Truth.assertThat(pushedEvent.uid() == downloadedEvent.uid()).isTrue()
        verifyEventCategoryAttributes(pushedEvent, downloadedEvent)
    }

    private suspend fun getTrackedEntityInstanceFromDB(trackedEntityInstanceUid: String): TrackedEntityInstance {
        return trackedEntityInstanceStore.selectByUid(trackedEntityInstanceUid)!!
    }

    private suspend fun getEnrollmentByTrackedEntityInstanceFromDb(trackedEntityInstanceUid: String): Enrollment =
        enrollmentStore.selectWhere(
            WhereClauseBuilder().appendKeyStringValue(
                EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                trackedEntityInstanceUid,
            ).build(),
        ).first()

    private suspend fun getEventFromDb(eventUid: String): Event =
        eventStore.selectAll().first { it.uid() == eventUid }

    @Throws(Exception::class)
    private fun postTrackedEntityInstances() {
        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()
    }

    @Throws(Exception::class)
    private fun downloadMetadata() {
        d2.userModule().logIn(username, password, url).blockingGet()

        d2.metadataModule().blockingDownload()
    }

    private fun verifyEventCategoryAttributes(event: Event, downloadedEvent: Event): Boolean {
        return event.uid() == downloadedEvent.uid() &&
            event.attributeOptionCombo() == downloadedEvent.attributeOptionCombo()
    }

    private val currentDateMinusTwoHoursTenMinutes: Date
        get() {
            val newTime = (Date()).time - (130 * 60 * 1000)
            return Date(newTime)
        }

    private fun getTEIUidFromRelationshipItem(item: RelationshipItem?): String? {
        if (item?.trackedEntityInstance() != null) {
            return item.trackedEntityInstance()?.trackedEntityInstance()
        }
        return null
    }
}
