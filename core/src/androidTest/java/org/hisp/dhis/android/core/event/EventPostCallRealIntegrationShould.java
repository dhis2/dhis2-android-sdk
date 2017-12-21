package org.hisp.dhis.android.core.event;

import static com.google.common.truth.Truth.assertThat;

import static junit.framework.Assert.assertTrue;

import android.content.ContentValues;
import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboModel;
import org.hisp.dhis.android.core.category.CategoryOptionComboModel;
import org.hisp.dhis.android.core.category.CategoryOptionModel;
import org.hisp.dhis.android.core.category.CreateCategoryComboUtils;
import org.hisp.dhis.android.core.category.CreateCategoryOptionUtils;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.SqliteCheckerUtility;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramType;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.utils.CodeGenerator;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Response;

@RunWith(AndroidJUnit4.class)
public class EventPostCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;
    Exception e;
    CodeGenerator codeGenerator;


    private EventStore eventStore;
    private TrackedEntityDataValueStore trackedEntityDataValueStore;

    private String eventUid;
    private String orgUnitUid;
    private String programUid;
    private String programStageUid;
    private String dataElementUid;
    private String attributeCategoryOption;
    private String attributeOptionCombo;
    private String user = "admin";
    private String password = "district";

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());

        eventStore = new EventStoreImpl(databaseAdapter());
        trackedEntityDataValueStore = new TrackedEntityDataValueStoreImpl(databaseAdapter());

        orgUnitUid = "ImspTQPwCqd";
        programUid = "kla3mAPgvCH";
        programStageUid = "aNLq9ZYoy9W";
        dataElementUid = "b6dOUjAarHD";
        attributeCategoryOption = "C6nZpLKjEJr";
        attributeOptionCombo = "nvLjum6Xbv5";
        codeGenerator = new CodeGeneratorImpl();

        eventUid = codeGenerator.generate();

    }

    private void createDummyDataToPost(String orgUnitUid, String programUid,
                                       String programStageUid, String eventUid,
                                       String dataElementUid, String attributeCategoryOption, String attributeOptionCombo) {
        eventStore.insert(
                eventUid, null, new Date(), new Date(), null, null,
                EventStatus.ACTIVE, "13.21", "12.21", programUid, programStageUid, orgUnitUid,
                new Date(), new Date(), new Date(), State.TO_POST, attributeCategoryOption, attributeOptionCombo
        );

        trackedEntityDataValueStore.insert(
                eventUid, new Date(), new Date(), dataElementUid, "user_name", "12", Boolean.FALSE
        );


    }

    // commented out since it is a flaky test that works against a real server.
    @Test
    public void successful_response_after_sync_events() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn(user, password).call();
        assertThat(response.isSuccessful()).isTrue();

        response = d2.syncMetaData().call();
        assertThat(response.isSuccessful()).isTrue();
        SqliteCheckerUtility.ifValueExist("CategoryOption", "uid", attributeCategoryOption, databaseAdapter());
        SqliteCheckerUtility.ifValueExist("CategoryOptionCombo", "uid", attributeOptionCombo, databaseAdapter());


        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid, dataElementUid, attributeCategoryOption, attributeOptionCombo);

        Call<Response<WebResponse>> call = d2.syncSingleEvents();
        response = call.call();
        assertThat(response.isSuccessful()).isTrue();


    }

    // commented out since it is a flaky test that works against a real server.
    @Test
    public void pull_event_with_correct_category_combo_after_be_pushed() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn(user, password).call();
        assertThat(response.isSuccessful()).isTrue();

        response = d2.syncMetaData().call();
        assertThat(response.isSuccessful()).isTrue();


        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid, dataElementUid, attributeCategoryOption, attributeOptionCombo);

        Call<Response<WebResponse>> call = d2.syncSingleEvents();
        response = call.call();
        assertThat(response.isSuccessful()).isTrue();

        EventStoreImpl eventStore = new EventStoreImpl(databaseAdapter());

        List<Event> storedEvents = eventStore.queryAll();

        Event event = null;
        for(Event storedEvent : storedEvents) {
            if(storedEvent.uid().equals(eventUid)) {
                event = storedEvent;
            }
        }

        d2.wipeDB().call();

        response = d2.logIn(user, password).call();
        assertThat(response.isSuccessful()).isTrue();

        response = d2.syncMetaData().call();
        assertThat(response.isSuccessful()).isTrue();

        //EventEndPointCall eventEndPointCall = EventCallFactory.create(
        //        d2.retrofit(), databaseAdapter(), orgUnitUid, attributeCategoryOption, attributeOptionCombo, 0);

        //response = eventEndPointCall.call();
        //assertThat(response.isSuccessful()).isTrue();

        eventStore = new EventStoreImpl(databaseAdapter());

        List<Event> downloadedEvents = eventStore.querySingleEvents();

        assertTrue(verifyPushedEventIsInPullList(event, downloadedEvents));
    }

    private boolean verifyPushedEventIsInPullList(Event event, List<Event> eventList) {
        for(Event pullEvent : eventList){
            if(event.uid().equals(pullEvent.uid) && event.attributeOptionCombo().equals(pullEvent.attributeOptionCombo()) && event.attributeCategoryOptions().equals(pullEvent.attributeCategoryOptions())){
                return true;
            }
        }
        return false;
    }

    @Test
    public void stub() throws Exception {

    }
}
