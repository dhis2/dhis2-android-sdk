package org.hisp.dhis.android.core.trackedentity;

import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.relationship.RelationshipModel;
import org.hisp.dhis.android.core.relationship.RelationshipStore;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStore;
import org.hisp.dhis.android.core.utils.CodeGenerator;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private ObjectWithoutUidStore<RelationshipModel> relationShipStore;
    private IdentifiableObjectStore<RelationshipTypeModel> relationshipTypeStore;
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

    private String categoryOptionUid;
    private String categoryComboOptionUid;


    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2= D2Factory.create("https://play.dhis2.org/android-current/api/", databaseAdapter());

        trackedEntityInstanceStore = new TrackedEntityInstanceStoreImpl(databaseAdapter());
        enrollmentStore = new EnrollmentStoreImpl(databaseAdapter());
        eventStore = new EventStoreImpl(databaseAdapter());
        trackedEntityAttributeValueStore = new TrackedEntityAttributeValueStoreImpl(databaseAdapter());
        trackedEntityDataValueStore = new TrackedEntityDataValueStoreImpl(databaseAdapter());
        relationShipStore = RelationshipStore.create(databaseAdapter());
        relationshipTypeStore = RelationshipTypeStore.create(databaseAdapter());

        codeGenerator = new CodeGeneratorImpl();
        orgUnitUid = "DiszpKrYNg8";
        programUid = "IpHINAT79UW";
        programStageUid = "A03MvHHogjR";
        dataElementUid = "a3kGcGDCuk6";
        trackedEntityUid = "nEenWmSyUEp";
        trackedEntityAttributeUid = "w75KJ2mc4zz";

        coordinates = "[9,9]";
        featureType = FeatureType.POINT;

        categoryOptionUid = "as6ygGvUGNg";
        categoryComboOptionUid = "bRowv6yZOF2";
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

        Call<Response<WebResponse>> call = d2.syncTrackedEntityInstances();
        response = call.call();

        assertThat(response.isSuccessful()).isTrue();
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

        d2.wipeDB().call();

        downloadMetadata();


        Call<List<TrackedEntityInstance>> trackedEntityInstanceByUidEndPointCall =
                TrackedEntityInstanceListDownloadAndPersistCall.create(
                        d2.databaseAdapter(), d2.retrofit(), Lists.newArrayList(trackedEntityInstanceUid));

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

        d2.wipeDB().call();
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

        d2.wipeDB().call();
        downloadMetadata();

        List<TrackedEntityInstance> teiList =  d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList(newUid1)).call();

        assertThat(teiList.size() == 1).isTrue();
    }

    //@Test
    public void post_new_relationship_to_client_created_tei() throws Exception {
        downloadMetadata();
        d2.downloadTrackedEntityInstances(5, true).call();

        TrackedEntityInstance teiA = trackedEntityInstanceStore.queryAll().values().iterator().next();
        String relationshipTypeUid = relationshipTypeStore.selectAll(RelationshipTypeModel.factory)
                .iterator().next().uid();

        // Create a TEI by copying an existing one
        String teiBUid = codeGenerator.generate();
        insertATei(teiBUid, teiA, FeatureType.MULTI_POLYGON);

        trackedEntityInstanceStore.setState(teiA.uid(), State.TO_POST);

        RelationshipModel relationshipModel = RelationshipModel.builder()
                .relationshipType(relationshipTypeUid)
                .trackedEntityInstanceA(teiA.uid())
                .trackedEntityInstanceB(teiBUid)
                .build();
        relationShipStore.updateOrInsertWhere(relationshipModel);

        Response teiSyncResponse = d2.syncTrackedEntityInstances().call();
        assertThat(teiSyncResponse.isSuccessful()).isTrue();

        d2.wipeDB().call();
        downloadMetadata();

        List<TrackedEntityInstance> responseTeiA =  d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList(teiA.uid())).call();
        assertThat(responseTeiA.size() == 1).isTrue();

        List<TrackedEntityInstance> responseTeiB =  d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList(teiBUid)).call();
        assertThat(responseTeiB.size() == 1).isTrue();

        Set<RelationshipModel> relationships = relationShipStore.selectAll(RelationshipModel.factory);
        assertThat(relationships).contains(relationshipModel);
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
        
        Date refDate = getCurrentDateMinusTenMinutes();

        trackedEntityInstanceStore.insert(trackedEntityInstanceUid, refDate, refDate, null,
                null, orgUnitUid, trackedEntityUid, coordinates, featureType, State.TO_POST);

        enrollmentStore.insert(
                enrollmentUid, refDate, refDate, null, null, orgUnitUid, programUid, refDate,
                refDate, Boolean.FALSE, EnrollmentStatus.ACTIVE,
                trackedEntityInstanceUid, "10.33", "12.231", State.TO_POST
        );

        eventStore.insert(
                eventUid, enrollmentUid, refDate, refDate, null, null,
                EventStatus.ACTIVE, "13.21", "12.21", programUid, programStageUid, orgUnitUid,
                refDate, refDate, refDate, State.TO_POST, categoryOptionUid, categoryComboOptionUid, trackedEntityInstanceUid
        );

        trackedEntityDataValueStore.insert(
                eventUid, refDate, refDate, dataElementUid, "user_name", "12", Boolean.FALSE
        );

        trackedEntityAttributeValueStore.insert(
                "new2", refDate, refDate, trackedEntityAttributeUid,
                trackedEntityInstanceUid
        );
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
        EnrollmentStoreImpl enrollmentStore = new EnrollmentStoreImpl(databaseAdapter());
        Enrollment enrollment = null;
        Map<String, List<Enrollment>> storedEnrollmentsByTrackedEntityInstance = enrollmentStore.queryAll();
        for(Enrollment storedEnrollment : storedEnrollmentsByTrackedEntityInstance.get(trackedEntityInstanceUid)) {
            if(storedEnrollment.uid().equals(enrollmentUid)) {
                enrollment = storedEnrollment;
            }
        }
        return enrollment;
    }

    private Event getEventsFromDb(String eventUid) {
        EventStoreImpl eventStore = new EventStoreImpl(databaseAdapter());
        Event event = null;
        List<Event> storedEvents = eventStore.queryAll();
        for(Event storedEvent : storedEvents) {
            if(storedEvent.uid().equals(eventUid)) {
                event = storedEvent;
            }
        }
        return event;
    }

    private void postTrackedEntityInstances() throws Exception {
        Response response;Call<Response<WebResponse>> call = d2.syncTrackedEntityInstances();
        response = call.call();

        assertThat(response.isSuccessful()).isTrue();
    }

    private void downloadMetadata() throws Exception {
        d2.logout().call();
        d2.logIn("android", "Android123").call();

        d2.syncMetaData().call();
    }

    private boolean verifyEventCategoryAttributes(Event event, Event downloadedEvent) {
            if(event.uid().equals(downloadedEvent.uid()) && event.attributeOptionCombo().equals(downloadedEvent.attributeOptionCombo()) && event.attributeCategoryOptions().equals(downloadedEvent.attributeCategoryOptions())){
                return true;
            }
        return false;
    }

    private Date getCurrentDateMinusTenMinutes() {
        Long newTime = (new Date()).getTime() - (10 * 60 * 1000);
        return new Date(newTime);
    }
}
