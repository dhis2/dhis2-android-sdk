package org.hisp.dhis.android.core.trackedentity;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.utils.CodeGenerator;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityInstancePostCallRealIntegrationShould extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    Exception e;
    CodeGenerator codeGenerator;

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
    private String programStageDataElementUid;
    private String trackedEntityAttributeUid;
    private String eventUid;
    private String enrollmentUid;
    private String trackedEntityInstanceUid;

    private String event1Uid;
    private String enrollment1Uid;
    private String trackedEntityInstance1Uid;


    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        ConfigurationModel config = ConfigurationModel.builder()
                .serverUrl(HttpUrl.parse("https://play.dhis2.org/dev/api/"))
                .build();

        d2 = new D2.Builder()
                .configuration(config)
                .databaseAdapter(databaseAdapter())
                .okHttpClient(
                        new OkHttpClient.Builder()
                                .addInterceptor(BasicAuthenticatorFactory.create(databaseAdapter()))
                                .build()
                ).build();

        trackedEntityInstanceStore = new TrackedEntityInstanceStoreImpl(databaseAdapter());
        enrollmentStore = new EnrollmentStoreImpl(databaseAdapter());
        eventStore = new EventStoreImpl(databaseAdapter());
        trackedEntityAttributeValueStore = new TrackedEntityAttributeValueStoreImpl(databaseAdapter());
        trackedEntityDataValueStore = new TrackedEntityDataValueStoreImpl(databaseAdapter());

        codeGenerator = new CodeGeneratorImpl();
        orgUnitUid = "DiszpKrYNg8";
        programUid = "IpHINAT79UW";
        programStageUid = "A03MvHHogjR";
        dataElementUid = "a3kGcGDCuk6";
        trackedEntityUid = "nEenWmSyUEp";
        programStageDataElementUid = "LBNxoXdMnkv";
        trackedEntityAttributeUid = "w75KJ2mc4zz";

        eventUid = codeGenerator.generate();
        enrollmentUid = codeGenerator.generate();
        trackedEntityInstanceUid = codeGenerator.generate();

        event1Uid = codeGenerator.generate();
        enrollment1Uid = codeGenerator.generate();
        trackedEntityInstance1Uid = codeGenerator.generate();
    }

    private void createDummyDataToPost(String orgUnitUid, String programUid, String programStageUid,
            String trackedEntityUid, String eventUid, String enrollmentUid,
            String trackedEntityInstanceUid, String trackedEntityAttributeUid,
            String dataElementUid) {
        trackedEntityInstanceStore.insert(
                trackedEntityInstanceUid, new Date(), new Date(), null, null, orgUnitUid, trackedEntityUid, State.TO_POST
        );

        enrollmentStore.insert(
                enrollmentUid, new Date(), new Date(), null, null, orgUnitUid, programUid, new Date(),
                new Date(), Boolean.FALSE, EnrollmentStatus.ACTIVE,
                trackedEntityInstanceUid, "10.33", "12.231", State.TO_POST
        );

        eventStore.insert(
                eventUid, enrollmentUid, new Date(), new Date(), null, null,
                EventStatus.ACTIVE, "13.21", "12.21", programUid, programStageUid, orgUnitUid,
                new Date(), new Date(), new Date(), State.TO_POST
        );

        trackedEntityDataValueStore.insert(
                eventUid, new Date(), new Date(), dataElementUid, "user_name", "value", Boolean.FALSE
        );

        trackedEntityAttributeValueStore.insert(
                "some_value", "2017-01-01", "2017-01-01", trackedEntityAttributeUid, trackedEntityInstanceUid
        );
    }

   /* How to extract database from tests:
    edit: AbsStoreTestCase.java (adding database name.)
    DbOpenHelper dbOpenHelper = new DbOpenHelper(InstrumentationRegistry.getTargetContext().getApplicationContext(),
    "test.db");
    make a debugger break point where desired (after sync complete)

    Then while on the breakpoint :
    Android/platform-tools/adb pull /data/user/0/org.hisp.dhis.android.test/databases/test.db test.db

    in datagrip:
    pragma foreign_keys = on;
    pragma foreign_key_check;*/

    //This test is uncommented because technically it is flaky.
    //It depends on a live server to operate and the login is hardcoded here.
    //Uncomment in order to quickly test changes vs a real server, but keep it uncommented after.
    @Test
    public void response_true_when_data_sync() throws Exception {
//        retrofit2.Response response = null;
//        response = d2.logIn("android", "Android123").call();
//        assertThat(response.isSuccessful()).isTrue();
//
//        response = d2.syncMetaData().call();
//        assertThat(response.isSuccessful()).isTrue();
//
//
//        createDummyDataToPost(
//                orgUnitUid, programUid, programStageUid, trackedEntityUid,
//                eventUid, enrollmentUid, trackedEntityInstanceUid, trackedEntityAttributeUid,
//                dataElementUid
//        );
//
//        createDummyDataToPost(
//                orgUnitUid, programUid, programStageUid, trackedEntityUid,
//                event1Uid, enrollment1Uid, trackedEntityInstance1Uid, trackedEntityAttributeUid,
//                dataElementUid
//        );
//
//
//        Call<Response<WebResponse>> call = d2.syncTrackedEntityInstances();
//        response = call.call();
//
//        assertThat(response.isSuccessful()).isTrue();
    }
}
