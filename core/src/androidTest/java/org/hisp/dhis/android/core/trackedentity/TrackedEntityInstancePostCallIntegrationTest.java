package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.enrollment.CreateEnrollmentUtils;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.event.CreateEventUtils;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.utils.CodeGenerator;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Response;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityInstancePostCallIntegrationTest extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    Exception e;
    CodeGenerator codeGenerator;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        ConfigurationModel config = ConfigurationModel.builder()
                .serverUrl(HttpUrl.parse("https://play.dhis2.org/demo/api/"))
                .build();

        d2 = new D2.Builder()
                .configuration(config)
                .databaseAdapter(databaseAdapter())
                .okHttpClient(
                        new OkHttpClient.Builder()
                                .addInterceptor(BasicAuthenticatorFactory.create(databaseAdapter()))
                                .build()
                ).build();

        codeGenerator = new CodeGeneratorImpl();
        String orgUnitUid = "DiszpKrYNg8";
        String programUid = "IpHINAT79UW";
        String programStageUid = "A03MvHHogjR";
        String dataElementUid = "a3kGcGDCuk6";
        String trackedEntityUid = "nEenWmSyUEp";
        String programStageDataElementUid = "LBNxoXdMnkv";
        String trackedEntityAttributeUid = "w75KJ2mc4zz";
        String eventUid = codeGenerator.generate();
        String enrollmentUid = codeGenerator.generate();
        String trackedEntityInstanceUid = codeGenerator.generate();
//
//
//        setUpMetadata(
//                orgUnitUid, programUid, programStageUid, programStageDataElementUid,
//                trackedEntityUid, dataElementUid, trackedEntityAttributeUid
//        );
//
//
//
        createDummyDataToPost(
                orgUnitUid, programUid, programStageUid, trackedEntityUid,
                eventUid, enrollmentUid, trackedEntityInstanceUid, trackedEntityAttributeUid,
                dataElementUid
        );

    }

    private void createDummyDataToPost(String orgUnitUid, String programUid, String programStageUid,
                                       String trackedEntityUid, String eventUid, String enrollmentUid,
                                       String trackedEntityInstanceUid, String trackedEntityAttributeUid,
                                       String dataElementUid) {
        ContentValues trackedEntityInstance =
                CreateTrackedEntityInstanceUtils.create(trackedEntityInstanceUid, orgUnitUid, trackedEntityUid);
        ContentValues enrollment =
                CreateEnrollmentUtils.create(enrollmentUid, programUid, orgUnitUid, trackedEntityInstanceUid);
        ContentValues event =
                CreateEventUtils.create(eventUid, programUid, programStageUid, orgUnitUid, enrollmentUid);
        ContentValues trackedEntityDataValues = CreateTrackedEntityDataValueUtils.create(1L, eventUid, dataElementUid);
        ContentValues trackedEntityAttributeValues =
                CreateTrackedEntityAttributeValueUtils.create(trackedEntityAttributeUid, trackedEntityInstanceUid);

        Transaction transaction = databaseAdapter().beginNewTransaction();
        try {
            database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);
            database().insert(EnrollmentModel.TABLE, null, enrollment);
            database().insert(EventModel.TABLE, null, event);
            database().insert(TrackedEntityDataValueModel.TABLE, null, trackedEntityDataValues);
            database().insert(TrackedEntityAttributeValueModel.TABLE, null, trackedEntityAttributeValues);
            transaction.setSuccessful();

        } finally {
            transaction.end();
        }


    }


    private void setUpMetadata(String orgUnitUid, String programUid,
                               String programStageUid, String programStageDataElementUid,
                               String trackedEntityUid,
                               String dataElementUid, String trackedEntityAttributeUid) {

//        ContentValues orgUnit = CreateOrganisationUnitUtils.createOrgUnit(1L, orgUnitUid);
//        ContentValues program = CreateProgramUtils.create(1L, programUid, null, null, trackedEntityUid);
//        ContentValues programStage = CreateProgramStageUtils.create(1L, programStageUid, programUid);
//
//
//        ContentValues programStageDataElement = CreateProgramStageDataElementUtils.create(
//                1L, programStageDataElementUid, programStageUid, dataElementUid
//        );
//
//        ContentValues dataElement = CreateDataElementUtils.create(1L, dataElementUid, null);
//
//
//        ContentValues trackedEntityAttribute = CreateTrackedEntityAttributeUtils.create(
//                1L, trackedEntityAttributeUid, null
//        );
//
//        database().insert(OrganisationUnitModel.TABLE, null, orgUnit);
//        database().insert(ProgramModel.TABLE, null, program);
//        database().insert(ProgramStageModel.TABLE, null, programStage);
//        database().insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement);
//        database().insert(DataElementModel.TABLE, null, dataElement);
//        database().insert(TrackedEntityAttributeModel.TABLE, null, trackedEntityAttribute);
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
    public void dataSyncTest() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn("android", "Android123").call();
        assertThat(response.isSuccessful()).isTrue();

        response = d2.syncMetaData().call();
        assertThat(response.isSuccessful()).isTrue();

        Call<Response<WebResponse>> call = d2.syncTrackedEntityInstances();
        response = call.call();

        assertThat(response.isSuccessful()).isTrue();

        //second sync:
//        response = d2.syncTrackedEntityInstances().call();
//        assertThat(response.isSuccessful()).isTrue();

        //TODO: add aditional sync + break point.
        //when debugger stops at the new break point manually change metadata online & resume.
        //This way I can make sure that additive (updates) work as well.
        //The changes could be to one of the programs, adding stuff to it.
        // adding a new program..etc.
    }

    @Test
    public void stub() {
    }
}
