package org.hisp.dhis.android.core.trackedentity;

import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyIdentifiableCollectionRepository;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentFields;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository;
import org.hisp.dhis.android.core.relationship.RelationshipHelper;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipModule;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.utils.CodeGenerator;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import retrofit2.Response;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityInstancePostCallRealIntegrationShould extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    private CodeGenerator codeGenerator;

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
    private FeatureType featureType;
    private String eventUid;
    private String enrollmentUid;
    private String trackedEntityInstanceUid;

    private String event1Uid;
    private String enrollment1Uid;
    private String trackedEntityInstance1Uid;

    private String categoryComboOptionUid;


    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2= D2Factory.create(RealServerMother.url, databaseAdapter());

        trackedEntityInstanceStore = new TrackedEntityInstanceStoreImpl(databaseAdapter());
        enrollmentStore = EnrollmentStoreImpl.create(databaseAdapter());
        eventStore = EventStoreImpl.create(databaseAdapter());
        trackedEntityAttributeValueStore = TrackedEntityAttributeValueStoreImpl.create(databaseAdapter());
        trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl.create(databaseAdapter());

        codeGenerator = new CodeGeneratorImpl();
        orgUnitUid = "DiszpKrYNg8";
        programUid = "IpHINAT79UW";
        programStageUid = "A03MvHHogjR";
        dataElementUid = "a3kGcGDCuk6";
        trackedEntityUid = "nEenWmSyUEp";
        trackedEntityAttributeUid = "w75KJ2mc4zz";

        coordinates = "[9,9]";
        featureType = FeatureType.POINT;

        categoryComboOptionUid = "HllvX50cXC0";
        eventUid = codeGenerator.generate();
        enrollmentUid = codeGenerator.generate();
        trackedEntityInstanceUid = codeGenerator.generate();

        event1Uid = codeGenerator.generate();
        enrollment1Uid = codeGenerator.generate();
        trackedEntityInstance1Uid = codeGenerator.generate();
    }


    @Test
    public void stub() throws Exception {

    }
    /*
    * If you want run this test you need config the correct uids in the server side.
    * At this moment is necessary add into the "child programme" program the category combo : Implementing Partner
    * */
    //@Test
    public void response_true_when_data_sync() throws Exception {

        Response response = null;
        downloadMetadata();


        createDummyDataToPost(
                orgUnitUid, programUid, programStageUid, trackedEntityUid, coordinates, featureType, eventUid,
                enrollmentUid, trackedEntityInstanceUid, trackedEntityAttributeUid, dataElementUid);

        createDummyDataToPost(
                orgUnitUid, programUid, programStageUid, trackedEntityUid, coordinates, featureType,
                event1Uid, enrollment1Uid, trackedEntityInstance1Uid, trackedEntityAttributeUid, dataElementUid);

        d2.syncTrackedEntityInstances().call();
    }


    /*
    * If you want run this test you need config the correct uids in the server side.
    * At this moment is necessary add into the "child programme" program the category combo : Implementing Partner
    * */

    //@Test
    public void pull_event_after_push_tracked_entity_instance_with_that_event() throws Exception {
        downloadMetadata();


        createDummyDataToPost(
                orgUnitUid, programUid, programStageUid, trackedEntityUid, coordinates, featureType,
                eventUid, enrollmentUid, trackedEntityInstanceUid, trackedEntityAttributeUid, dataElementUid);

        postTrackedEntityInstances();

        TrackedEntityInstance pushedTrackedEntityInstance = getTrackedEntityInstanceFromDB(trackedEntityInstanceUid);
        Enrollment pushedEnrollment = getEnrollmentsByTrackedEntityInstanceFromDb(trackedEntityInstanceUid);
        Event pushedEvent = getEventsFromDb(eventUid);

        d2.wipeModule().wipeEverything();

        downloadMetadata();


        Callable<List<TrackedEntityInstance>> trackedEntityInstanceByUidEndPointCall =
                d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList(trackedEntityInstanceUid));

        trackedEntityInstanceByUidEndPointCall.call();

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
        d2.downloadTrackedEntityInstances(4, true).call();

        TrackedEntityInstance tei = trackedEntityInstanceStore.queryAll().values().iterator().next();

        FeatureType featureType =
                tei.featureType() == FeatureType.POLYGON ? FeatureType.POINT : FeatureType.POLYGON;

        String newUid = codeGenerator.generate();

        insertATei(newUid, tei, featureType);

        d2.syncTrackedEntityInstances().call();

        d2.wipeModule().wipeEverything();
        downloadMetadata();

        List<TrackedEntityInstance> response =  d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList(newUid)).call();

        TrackedEntityInstance updatedTei = response.get(0);

        assertThat(updatedTei.featureType()).isEqualTo(featureType);
    }

    //@Test
    public void post_more_than_one_tei() throws Exception {
        downloadMetadata();
        d2.downloadTrackedEntityInstances(4, true).call();

        TrackedEntityInstance tei = trackedEntityInstanceStore.queryAll().values().iterator().next();

        String newUid1 = codeGenerator.generate();
        String newUid2 = codeGenerator.generate();

        insertATei(newUid1, tei, tei.featureType());
        insertATei(newUid2, tei, tei.featureType());

        d2.syncTrackedEntityInstances().call();

        d2.wipeModule().wipeEverything();
        downloadMetadata();

        List<TrackedEntityInstance> teiList =  d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList(newUid1)).call();

        assertThat(teiList.size() == 1).isTrue();
    }

    /* Set Dhis2 server to 2.30 or up*/
    //@Test
    public void post_one_tei_and_delete_it() throws Exception {
        downloadMetadata();
        d2.downloadTrackedEntityInstances(1, true).call();

        TrackedEntityInstance tei = trackedEntityInstanceStore.queryAll().values().iterator().next();

        FeatureType featureType =
                tei.featureType() == FeatureType.POLYGON ? FeatureType.POINT : FeatureType.POLYGON;

        String newUid = codeGenerator.generate();

        insertATei(newUid, tei, featureType);

        d2.syncTrackedEntityInstances().call();
        List<TrackedEntityInstance> response =
                d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList(newUid)).call();
        assertThat(response.size()).isEqualTo(1);

        trackedEntityInstanceStore.setState(newUid, State.TO_DELETE);

        d2.syncTrackedEntityInstances().call();

        try {
            d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList(newUid)).call();
        } catch (D2Error e) {
            assertThat(e.errorComponent()).isEqualTo(D2ErrorComponent.Server);
            assertThat(e.errorCode()).isEqualTo(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE);
        }
    }

    //@Test
    public void post_new_relationship_to_client_created_tei() throws Exception {
        downloadMetadata();
        d2.downloadTrackedEntityInstances(5, true).call();

        TrackedEntityInstance teiA = trackedEntityInstanceStore.queryAll().values().iterator().next();
        RelationshipType relationshipType = d2.relationshipModule().relationshipTypes.get().iterator().next();

        // Create a TEI by copying an existing one
        String teiBUid = codeGenerator.generate();
        insertATei(teiBUid, teiA, FeatureType.MULTI_POLYGON);

        trackedEntityInstanceStore.setState(teiA.uid(), State.TO_POST);

        Relationship newRelationship = RelationshipHelper.teiToTeiRelationship(teiA.uid(),
                teiBUid, relationshipType.uid());
        d2.relationshipModule().relationships.add(newRelationship);

        d2.syncTrackedEntityInstances().call();

        d2.wipeModule().wipeEverything();
        downloadMetadata();

        List<TrackedEntityInstance> responseTeiA =  d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList(teiA.uid())).call();
        assertThat(responseTeiA.size() == 1).isTrue();

        List<TrackedEntityInstance> responseTeiB =  d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList(teiBUid)).call();
        assertThat(responseTeiB.size() == 1).isTrue();

        List<Relationship> relationships = d2.relationshipModule().relationships.getByItem(RelationshipHelper.teiItem(teiA.uid()));
        assertThat(relationships.size() > 0).isTrue();

        Boolean relationshipFound = false;
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

        d2.downloadTrackedEntityInstances(5,  false).call();
        Map<String, TrackedEntityInstance> trackedEntityInstances =
                new TrackedEntityInstanceStoreImpl(databaseAdapter()).queryAll();
        assertThat(trackedEntityInstances.size() >= 5).isTrue();

        Iterator<Map.Entry<String, TrackedEntityInstance>> tEIIterator = trackedEntityInstances.entrySet().iterator();

        TrackedEntityInstance t0 = tEIIterator.next().getValue();
        TrackedEntityInstance t1 = tEIIterator.next().getValue();

        RelationshipType relationshipType = d2.relationshipModule().relationshipTypes.get().iterator().next();

        d2.relationshipModule().relationships.add(RelationshipHelper.teiToTeiRelationship(t0.uid(), t1.uid(),
                relationshipType.uid()));

        d2.syncTrackedEntityInstances().call();

        d2.syncDownSyncedTrackedEntityInstances().call();
    }

    //@Test
    public void create_and_delete_tei_to_tei_relationship() throws Exception {
        downloadMetadata();

        d2.downloadTrackedEntityInstances(10,  false).call();
        Map<String, TrackedEntityInstance> trackedEntityInstances =
                new TrackedEntityInstanceStoreImpl(databaseAdapter()).queryAll();
        assertThat(trackedEntityInstances.size() == 10).isTrue();

        Iterator<Map.Entry<String, TrackedEntityInstance>> tEIIterator = trackedEntityInstances.entrySet().iterator();

        TrackedEntityInstance t0 = tEIIterator.next().getValue();
        TrackedEntityInstance t1 = tEIIterator.next().getValue();

        RelationshipModule relationshipModule = d2.relationshipModule();
        ReadOnlyIdentifiableCollectionRepository<RelationshipType> typesRepository = relationshipModule.relationshipTypes;
        RelationshipCollectionRepository relationshipsRepository = relationshipModule.relationships;

        RelationshipType relationshipType = typesRepository.get().iterator().next();

        Relationship newRelationship = RelationshipHelper.teiToTeiRelationship(t0.uid(), t1.uid(),
                relationshipType.uid());
        relationshipsRepository.add(newRelationship);

        d2.syncTrackedEntityInstances().call();

        relationshipsRepository.uid(newRelationship.uid()).delete();

        d2.syncTrackedEntityInstances().call();
    }

    //@Test
    public void post_a_tei_and_delete_one_event() throws Exception {
        downloadMetadata();
        d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList("LxMVYhJm3Jp")).call();

        TrackedEntityInstance tei = trackedEntityInstanceStore.queryAll().values().iterator().next();

        Enrollment enrollment = enrollmentStore.selectFirst();

        Event event = eventStore.selectFirst();
        String eventUid = event.uid();

        trackedEntityInstanceStore.setState(tei.uid(), State.TO_UPDATE);
        enrollmentStore.setState(enrollment.uid(), State.TO_UPDATE);
        eventStore.setState(eventUid, State.TO_DELETE);

        d2.syncTrackedEntityInstances().call();

        d2.wipeModule().wipeEverything();
        downloadMetadata();
        d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList("LxMVYhJm3Jp")).call();

        Boolean deleted = true;
        for (Event eventToCheck : eventStore.selectAll()) {
            if (eventToCheck.uid().equals(eventUid)) {
                deleted = false;
            }
        }

        assertThat(deleted).isEqualTo(true);
    }

    private void insertATei(String uid, TrackedEntityInstance tei, FeatureType featureType) {
        trackedEntityInstanceStore.insert(uid, tei.created(), tei.lastUpdated(), tei.createdAtClient(),
                tei.lastUpdatedAtClient(), tei.organisationUnit(), tei.trackedEntityType(), tei.coordinates(),
                featureType, State.TO_POST);
    }

    private void createDummyDataToPost(String orgUnitUid, String programUid, String programStageUid,
                                       String trackedEntityUid, String coordinates, FeatureType featureType,
                                       String eventUid, String enrollmentUid, String trackedEntityInstanceUid,
                                       String trackedEntityAttributeUid, String dataElementUid) {
        
        Date refDate = getCurrentDateMinusTwoHoursTenMinutes();

        trackedEntityInstanceStore.insert(trackedEntityInstanceUid, refDate, refDate, null,
                null, orgUnitUid, trackedEntityUid, coordinates, featureType, State.TO_POST);

        Enrollment enrollment = Enrollment.builder()
                .uid(enrollmentUid).created(refDate).lastUpdated(refDate).organisationUnit(orgUnitUid)
                .program(programUid).incidentDate(refDate).enrollmentDate(refDate).followUp(Boolean.FALSE)
                .status(EnrollmentStatus.ACTIVE).trackedEntityInstance(trackedEntityInstanceUid)
                .coordinate(Coordinates.create(10.33, 12.231)).state(State.TO_POST).build();

        enrollmentStore.insert(enrollment);

        Event event = Event.builder()
                .uid(eventUid).enrollment(enrollmentUid).created(refDate).lastUpdated(refDate)
                .status(EventStatus.ACTIVE).coordinate(Coordinates.create(13.21, 12.21)).program(programUid)
                .programStage(programStageUid).organisationUnit(orgUnitUid).eventDate(refDate).dueDate(refDate)
                .completedDate(refDate).state(State.TO_POST).attributeOptionCombo(categoryComboOptionUid)
                .trackedEntityInstance(trackedEntityInstanceUid).build();

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
        TrackedEntityInstanceStore trackedEntityInstanceStore = new TrackedEntityInstanceStoreImpl(databaseAdapter());
        TrackedEntityInstance trackedEntityInstance = null;
        Map<String, TrackedEntityInstance> storedTrackedEntityInstances = trackedEntityInstanceStore.queryAll();
        TrackedEntityInstance storedTrackedEntityInstance = storedTrackedEntityInstances.get(trackedEntityInstanceUid);
        if(storedTrackedEntityInstance.uid().equals(trackedEntityInstanceUid)) {
            trackedEntityInstance = storedTrackedEntityInstance;
        }
        return trackedEntityInstance;
    }

    private Enrollment getEnrollmentsByTrackedEntityInstanceFromDb(String trackedEntityInstanceUid) {
        EnrollmentStore enrollmentStore = EnrollmentStoreImpl.create(databaseAdapter());
        Enrollment enrollment = null;
        List<Enrollment> storedEnrollments = enrollmentStore.selectWhereClause(new WhereClauseBuilder()
                .appendKeyStringValue(EnrollmentFields.TRACKED_ENTITY_INSTANCE, trackedEntityInstanceUid).build());
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
        d2.syncTrackedEntityInstances().call();
    }

    private void downloadMetadata() throws Exception {
        d2.userModule().logIn("android", "Android123").call();

        d2.syncMetaData().call();
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