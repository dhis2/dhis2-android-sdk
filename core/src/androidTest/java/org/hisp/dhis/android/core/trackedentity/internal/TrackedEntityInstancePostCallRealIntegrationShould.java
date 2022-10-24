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

package org.hisp.dhis.android.core.trackedentity.internal;

import static com.google.common.truth.Truth.assertThat;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.helpers.UidGenerator;
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.event.internal.EventStore;
import org.hisp.dhis.android.core.event.internal.EventStoreImpl;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository;
import org.hisp.dhis.android.core.relationship.RelationshipHelper;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipModule;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection;
import org.hisp.dhis.android.core.tracker.exporter.TrackerD2Progress;
import org.junit.Before;

import java.util.Date;
import java.util.List;

import io.reactivex.observers.TestObserver;

public class TrackedEntityInstancePostCallRealIntegrationShould extends BaseRealIntegrationTest {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private UidGenerator uidGenerator;

    private TrackedEntityInstanceStore trackedEntityInstanceStore;
    private EnrollmentStore enrollmentStore;
    private EventStore eventStore;
    private TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;
    private TrackedEntityDataValueStore trackedEntityDataValueStore;
    private String orgUnitUid;
    private String programUid;
    private String programStageUid;
    private String dataElementUid;
    private String trackedEntityUid;
    private String trackedEntityAttributeUid;
    private String coordinates;
    private Geometry geometry;
    private String eventUid;
    private String enrollmentUid;
    private String trackedEntityInstanceUid;

    private String event1Uid;
    private String enrollment1Uid;
    private String trackedEntityInstance1Uid;

    private String categoryComboOptionUid;


    @Before
    @Override
    public void setUp() {
        super.setUp();

        trackedEntityInstanceStore = TrackedEntityInstanceStoreImpl.create(d2.databaseAdapter());
        enrollmentStore = EnrollmentStoreImpl.create(d2.databaseAdapter());
        eventStore = EventStoreImpl.create(d2.databaseAdapter());
        trackedEntityAttributeValueStore = TrackedEntityAttributeValueStoreImpl.create(d2.databaseAdapter());
        trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl.create(d2.databaseAdapter());

        uidGenerator = new UidGeneratorImpl();
        orgUnitUid = "DiszpKrYNg8";
        programUid = "IpHINAT79UW";
        programStageUid = "A03MvHHogjR";
        dataElementUid = "a3kGcGDCuk6";
        trackedEntityUid = "nEenWmSyUEp";
        trackedEntityAttributeUid = "w75KJ2mc4zz";

        coordinates = "[9,9]";
        geometry = Geometry.builder().type(FeatureType.POINT).coordinates("[-11.96, 9.49]").build();

        categoryComboOptionUid = "HllvX50cXC0";
        eventUid = uidGenerator.generate();
        enrollmentUid = uidGenerator.generate();
        trackedEntityInstanceUid = uidGenerator.generate();

        event1Uid = uidGenerator.generate();
        enrollment1Uid = uidGenerator.generate();
        trackedEntityInstance1Uid = uidGenerator.generate();
    }

    /*
    * If you want run this test you need config the correct uids in the server side.
    * At this moment is necessary add into the "child programme" program the category combo : Implementing Partner
    * */
    //@Test
    public void response_true_when_data_sync() throws Exception {
        downloadMetadata();


        createDummyDataToPost(
                orgUnitUid, programUid, programStageUid, trackedEntityUid, coordinates, geometry, eventUid,
                enrollmentUid, trackedEntityInstanceUid, trackedEntityAttributeUid, dataElementUid);

        createDummyDataToPost(
                orgUnitUid, programUid, programStageUid, trackedEntityUid, coordinates, geometry,
                event1Uid, enrollment1Uid, trackedEntityInstance1Uid, trackedEntityAttributeUid, dataElementUid);

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();
    }

    //@Test
    public void add_and_post_tei_using_repositories() throws Exception {

        downloadMetadata();

        String childProgramUid = "IpHINAT79UW";


        // Organisation unit module -> get one organisation unit
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits().one().blockingGet();

        // Program module -> get the program by its uid
        Program program = d2.programModule().programs()
                .uid(childProgramUid)
                .blockingGet();

        // Tracked entity module -> add a new tracked entity instance
        String teiUid = d2.trackedEntityModule().trackedEntityInstances()
                .blockingAdd(TrackedEntityInstanceCreateProjection.builder()
                        .organisationUnit(organisationUnit.uid())
                        .trackedEntityType(program.trackedEntityType().uid())
                        .build());

        // Enrollment module -> enroll the tracked entity instance to the program
        d2.enrollmentModule().enrollments().blockingAdd(
                EnrollmentCreateProjection.builder()
                        .organisationUnit(organisationUnit.uid())
                        .program(program.uid())
                        .trackedEntityInstance(teiUid)
                        .build()
        );

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

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();
    }


    /*
    * If you want run this test you need config the correct uids in the server side.
    * At this moment is necessary add into the "child programme" program the category combo : Implementing Partner
    * */

    //@Test
    public void pull_event_after_push_tracked_entity_instance_with_that_event() throws Exception {
        downloadMetadata();


        createDummyDataToPost(
                orgUnitUid, programUid, programStageUid, trackedEntityUid, coordinates, geometry,
                eventUid, enrollmentUid, trackedEntityInstanceUid, trackedEntityAttributeUid, dataElementUid);

        postTrackedEntityInstances();

        TrackedEntityInstance pushedTrackedEntityInstance = getTrackedEntityInstanceFromDB(trackedEntityInstanceUid);
        Enrollment pushedEnrollment = getEnrollmentsByTrackedEntityInstanceFromDb(trackedEntityInstanceUid);
        Event pushedEvent = getEventsFromDb(eventUid);

        d2.wipeModule().wipeEverything();

        downloadMetadata();


        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(trackedEntityInstanceUid)
                .blockingDownload();

        TrackedEntityInstance downloadedTrackedEntityInstance = getTrackedEntityInstanceFromDB(trackedEntityInstanceUid);
        Enrollment downloadedEnrollment = getEnrollmentsByTrackedEntityInstanceFromDb(trackedEntityInstanceUid);
        Event downloadedEvent = getEventsFromDb(eventUid);

        assertPushAndDownloadTrackedEntityInstances(pushedTrackedEntityInstance, pushedEnrollment,
                pushedEvent, downloadedTrackedEntityInstance, downloadedEnrollment,
                downloadedEvent);
    }

    //@Test
    public void post_a_tei() throws Exception {
        downloadMetadata();
        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(4).limitByOrgunit(true).blockingDownload();

        TrackedEntityInstance tei = trackedEntityInstanceStore.selectFirst();

        Geometry geometry = Geometry.builder().type(FeatureType.POINT).coordinates("[98.54, 4.65]").build();

        String newUid = uidGenerator.generate();

        insertATei(newUid, tei, geometry);

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();

        d2.wipeModule().wipeEverything();
        downloadMetadata();

        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(newUid).blockingDownload();

        List<TrackedEntityInstance> response = d2.trackedEntityModule().trackedEntityInstances().byUid().eq(newUid).blockingGet();

        TrackedEntityInstance updatedTei = response.get(0);

        assertThat(updatedTei.geometry()).isEqualTo(geometry);
    }

    //@Test
    public void post_more_than_one_tei() throws Exception {
        downloadMetadata();
        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(4).limitByOrgunit(true).blockingDownload();

        TrackedEntityInstance tei = trackedEntityInstanceStore.selectFirst();

        String newUid1 = uidGenerator.generate();
        String newUid2 = uidGenerator.generate();

        insertATei(newUid1, tei, tei.geometry());
        insertATei(newUid2, tei, tei.geometry());

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();

        d2.wipeModule().wipeEverything();
        downloadMetadata();

        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(newUid1).blockingDownload();

        List<TrackedEntityInstance> teiList =  d2.trackedEntityModule().trackedEntityInstances().byUid().eq(newUid1).blockingGet();

        assertThat(teiList.size() == 1).isTrue();
    }

    //@Test
    public void post_teis_filtering_what_to_post() throws Exception {
        downloadMetadata();
        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(4).limitByOrgunit(true).blockingDownload();

        TrackedEntityInstance tei = trackedEntityInstanceStore.selectFirst();

        String newUid1 = uidGenerator.generate();
        String newUid2 = uidGenerator.generate();

        insertATei(newUid1, tei, tei.geometry());
        insertATei(newUid2, tei, tei.geometry());

        d2.trackedEntityModule().trackedEntityInstances().byUid().eq(newUid1).blockingUpload();

        d2.wipeModule().wipeEverything();
        downloadMetadata();

        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(newUid1).blockingDownload();

        List<TrackedEntityInstance> teiList =  d2.trackedEntityModule().trackedEntityInstances().byUid().eq(newUid1).blockingGet();

        assertThat(teiList.size() == 1).isTrue();

        boolean teiDownloadedSuccessfully = true;
        try {
            d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(newUid2).blockingDownload();
        } catch (Exception e) {
            teiDownloadedSuccessfully = false;
        }
        assertThat(teiDownloadedSuccessfully).isFalse();
    }

    /* Set Dhis2 server to 2.30 or up*/
    //@Test
    public void post_one_tei_and_delete_it() throws Exception {
        downloadMetadata();
        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(1).limitByOrgunit(true).blockingDownload();

        TrackedEntityInstance tei = trackedEntityInstanceStore.selectFirst();

        Geometry geometry = Geometry.builder().type(FeatureType.POINT).coordinates("[98.54, 4.65]").build();

        String newUid = uidGenerator.generate();

        insertATei(newUid, tei, geometry);

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();
        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(newUid).blockingDownload();

        List<TrackedEntityInstance> response =  d2.trackedEntityModule().trackedEntityInstances().byUid().eq(newUid).blockingGet();

        assertThat(response.size()).isEqualTo(1);

        d2.trackedEntityModule().trackedEntityInstances().uid(newUid).blockingDelete();

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();

        TestObserver<TrackerD2Progress> testObserver =
                d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(newUid).download().test();
        testObserver.awaitTerminalEvent();

        D2Error e = (D2Error) testObserver.errors().get(0);

        assertThat(e.errorComponent()).isEqualTo(D2ErrorComponent.Server);
        assertThat(e.errorCode()).isEqualTo(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE);
    }

    //@Test
    public void post_new_relationship_to_client_created_tei() throws Exception {
        downloadMetadata();
        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(5).limitByOrgunit(true).blockingDownload();

        TrackedEntityInstance teiA = trackedEntityInstanceStore.selectFirst();
        RelationshipType relationshipType = d2.relationshipModule().relationshipTypes().blockingGet().iterator().next();
        Geometry geometry = Geometry.builder().type(FeatureType.MULTI_POLYGON).coordinates("[98.54, 4.65]").build();

        // Create a TEI by copying an existing one
        String teiBUid = uidGenerator.generate();
        insertATei(teiBUid, teiA, geometry);

        trackedEntityInstanceStore.setSyncState(teiA.uid(), State.TO_POST);

        Relationship newRelationship = RelationshipHelper.teiToTeiRelationship(teiA.uid(),
                teiBUid, relationshipType.uid());
        d2.relationshipModule().relationships().blockingAdd(newRelationship);

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();

        d2.wipeModule().wipeEverything();
        downloadMetadata();

        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(teiA.uid()).blockingDownload();
        List<TrackedEntityInstance> responseTeiA = d2.trackedEntityModule().trackedEntityInstances().byUid().eq(teiA.uid()).blockingGet();
        assertThat(responseTeiA.size() == 1).isTrue();


        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(teiBUid).blockingDownload();
        List<TrackedEntityInstance> responseTeiB = d2.trackedEntityModule().trackedEntityInstances().byUid().eq(teiBUid).blockingGet();
        assertThat(responseTeiB.size() == 1).isTrue();

        List<Relationship> relationships =
                d2.relationshipModule().relationships().getByItem(RelationshipHelper.teiItem(teiA.uid()), true, false);
        assertThat(relationships.size() > 0).isTrue();

        boolean relationshipFound = false;
        for (Relationship relationship : relationships) {
            if (!relationshipType.uid().equals(relationship.relationshipType())) {
                break;
            }
            String fromUid = getTEIUidFromRelationshipItem(relationship.from());
            String toUid = getTEIUidFromRelationshipItem(relationship.to());

            if (teiA.uid().equals(fromUid) && teiBUid.equals(toUid)) {
                relationshipFound = true;
            }
        }
        assertThat(relationshipFound).isTrue();
    }

    //@Test
    public void create_tei_to_tei_relationship() throws Exception {
        downloadMetadata();

        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(5).blockingDownload();
        List<TrackedEntityInstance> trackedEntityInstances = trackedEntityInstanceStore.selectAll();
        assertThat(trackedEntityInstances.size() >= 5).isTrue();

        TrackedEntityInstance t0 = trackedEntityInstances.get(0);
        TrackedEntityInstance t1 = trackedEntityInstances.get(1);

        RelationshipType relationshipType = d2.relationshipModule().relationshipTypes().blockingGet().iterator().next();

        d2.relationshipModule().relationships().blockingAdd(RelationshipHelper.teiToTeiRelationship(t0.uid(), t1.uid(),
                relationshipType.uid()));

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();
    }

    //@Test
    public void create_and_delete_tei_to_tei_relationship() throws Exception {
        downloadMetadata();

        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(10).blockingDownload();
        List<TrackedEntityInstance> trackedEntityInstances = trackedEntityInstanceStore.selectAll();

        assertThat(trackedEntityInstances.size() == 10).isTrue();

        TrackedEntityInstance t0 = trackedEntityInstances.get(0);
        TrackedEntityInstance t1 = trackedEntityInstances.get(1);

        RelationshipModule relationshipModule = d2.relationshipModule();
        RelationshipTypeCollectionRepository typesRepository = relationshipModule.relationshipTypes();
        RelationshipCollectionRepository relationshipsRepository = relationshipModule.relationships();

        RelationshipType relationshipType = typesRepository.blockingGet().iterator().next();

        Relationship newRelationship = RelationshipHelper.teiToTeiRelationship(t0.uid(), t1.uid(),
                relationshipType.uid());
        relationshipsRepository.blockingAdd(newRelationship);

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();

        relationshipsRepository.uid(newRelationship.uid()).blockingDelete();

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();
    }

    //@Test
    public void post_a_tei_and_delete_one_event() throws Exception {
        downloadMetadata();
        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq("LxMVYhJm3Jp").blockingDownload();

        Event event = eventStore.selectFirst();
        String eventUid = event.uid();

        d2.eventModule().events().uid(eventUid).blockingDelete();

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();

        d2.wipeModule().wipeEverything();
        downloadMetadata();
        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq("LxMVYhJm3Jp").blockingDownload();

        Boolean deleted = true;
        for (Event eventToCheck : eventStore.selectAll()) {
            if (eventToCheck.uid().equals(eventUid)) {
                deleted = false;
            }
        }

        assertThat(deleted).isTrue();
    }

    private void insertATei(String uid, TrackedEntityInstance tei, Geometry geometry) {
        TrackedEntityInstance trackedEntityInstance = tei.toBuilder()
                .uid(uid)
                .geometry(geometry)
                .syncState(State.TO_POST)
                .aggregatedSyncState(State.TO_POST)
                .build();

        trackedEntityInstanceStore.insert(trackedEntityInstance);
    }

    private void createDummyDataToPost(String orgUnitUid, String programUid, String programStageUid,
                                       String trackedEntityUid, String coordinates, Geometry geometry,
                                       String eventUid, String enrollmentUid, String trackedEntityInstanceUid,
                                       String trackedEntityAttributeUid, String dataElementUid) {
        
        Date refDate = getCurrentDateMinusTwoHoursTenMinutes();

        TrackedEntityInstance trackedEntityInstance = TrackedEntityInstance.builder()
                .uid(trackedEntityInstanceUid)
                .created(refDate)
                .lastUpdated(refDate)
                .organisationUnit(orgUnitUid)
                .trackedEntityType(trackedEntityUid)
                .geometry(geometry)
                .syncState(State.TO_POST)
                .aggregatedSyncState(State.TO_POST)
                .build();

        trackedEntityInstanceStore.insert(trackedEntityInstance);

        Enrollment enrollment = Enrollment.builder()
                .uid(enrollmentUid).created(refDate).lastUpdated(refDate).organisationUnit(orgUnitUid)
                .program(programUid).incidentDate(refDate).completedDate(refDate).enrollmentDate(refDate)
                .followUp(Boolean.FALSE).status(EnrollmentStatus.ACTIVE).trackedEntityInstance(trackedEntityInstanceUid)
                .geometry(Geometry.builder().type(FeatureType.POINT).coordinates("[10.33, 12.231]").build())
                .syncState(State.TO_POST).aggregatedSyncState(State.TO_POST).build();

        enrollmentStore.insert(enrollment);

        Event event = Event.builder()
                .uid(eventUid).enrollment(enrollmentUid).created(refDate).lastUpdated(refDate)
                .status(EventStatus.ACTIVE).program(programUid)
                .geometry(Geometry.builder().type(FeatureType.POINT).coordinates("[12.21, 13.21]").build())
                .programStage(programStageUid).organisationUnit(orgUnitUid).eventDate(refDate).dueDate(refDate)
                .completedDate(refDate).syncState(State.TO_POST).attributeOptionCombo(categoryComboOptionUid)
                .build();

        eventStore.insert(event);

        TrackedEntityDataValue trackedEntityDataValue = TrackedEntityDataValue.builder()
                .event(eventUid)
                .created(refDate)
                .lastUpdated(refDate)
                .dataElement(dataElementUid)
                .storedBy("user_name")
                .value("12")
                .providedElsewhere(Boolean.FALSE)
                .build();

        trackedEntityDataValueStore.insert(trackedEntityDataValue);

        TrackedEntityAttributeValue trackedEntityAttributeValue = TrackedEntityAttributeValue.builder()
                .value("new2").created(refDate).lastUpdated(refDate).trackedEntityAttribute(trackedEntityAttributeUid)
                .trackedEntityInstance(trackedEntityInstanceUid).build();

        trackedEntityAttributeValueStore.insert(trackedEntityAttributeValue);
    }

    private void assertPushAndDownloadTrackedEntityInstances(
            TrackedEntityInstance pushedTrackedEntityInstance, Enrollment pushedEnrollment,
            Event pushedEvent, TrackedEntityInstance downloadedTrackedEntityInstance,
            Enrollment downloadedEnrollment, Event downloadedEvent) {
        assertThat(pushedTrackedEntityInstance.uid().equals(downloadedTrackedEntityInstance.uid())).isTrue();
        assertThat(pushedTrackedEntityInstance.uid().equals(downloadedTrackedEntityInstance.uid())).isTrue();
        assertThat(pushedEnrollment.uid().equals(downloadedEnrollment.uid())).isTrue();
        assertThat(pushedEvent.uid().equals(downloadedEvent.uid())).isTrue();
        assertThat(pushedEvent.uid().equals(downloadedEvent.uid())).isTrue();
        verifyEventCategoryAttributes(pushedEvent, downloadedEvent);
    }

    private TrackedEntityInstance getTrackedEntityInstanceFromDB(String trackedEntityInstanceUid) {
        TrackedEntityInstance trackedEntityInstance = null;
        TrackedEntityInstance storedTrackedEntityInstance = trackedEntityInstanceStore.selectByUid(trackedEntityInstanceUid);
        if(storedTrackedEntityInstance.uid().equals(trackedEntityInstanceUid)) {
            trackedEntityInstance = storedTrackedEntityInstance;
        }
        return trackedEntityInstance;
    }

    private Enrollment getEnrollmentsByTrackedEntityInstanceFromDb(String trackedEntityInstanceUid) {
        EnrollmentStore enrollmentStore = EnrollmentStoreImpl.create(d2.databaseAdapter());
        Enrollment enrollment = null;
        List<Enrollment> storedEnrollments = enrollmentStore.selectWhere(new WhereClauseBuilder()
                .appendKeyStringValue(EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE, trackedEntityInstanceUid).build());
        for (Enrollment storedEnrollment : storedEnrollments) {
            if(storedEnrollment.uid().equals(enrollmentUid)) {
                enrollment = storedEnrollment;
            }
        }
        return enrollment;
    }

    private Event getEventsFromDb(String eventUid) {
        Event event = null;
        List<Event> storedEvents = eventStore.selectAll();
        for(Event storedEvent : storedEvents) {
            if(storedEvent.uid().equals(eventUid)) {
                event = storedEvent;
            }
        }
        return event;
    }

    private void postTrackedEntityInstances() throws Exception {
        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();
    }

    private void downloadMetadata() throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();

        d2.metadataModule().blockingDownload();
    }

    private boolean verifyEventCategoryAttributes(Event event, Event downloadedEvent) {
        return event.uid().equals(downloadedEvent.uid()) &&
                event.attributeOptionCombo().equals(downloadedEvent.attributeOptionCombo());
    }

    private Date getCurrentDateMinusTwoHoursTenMinutes() {
        Long newTime = (new Date()).getTime() - (130 * 60 * 1000);
        return new Date(newTime);
    }

    private String getTEIUidFromRelationshipItem(RelationshipItem item) {
        if (item != null && item.trackedEntityInstance() != null) {
            return item.trackedEntityInstance().trackedEntityInstance();
        }
        return null;
    }
}