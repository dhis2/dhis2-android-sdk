package org.hisp.dhis.android.core.event;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.utils.CodeGenerator;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Response;

import static com.google.common.truth.Truth.assertThat;

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

    @Override
    @Before
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

        eventStore = new EventStoreImpl(databaseAdapter());
        trackedEntityDataValueStore = new TrackedEntityDataValueStoreImpl(databaseAdapter());

        orgUnitUid = "DiszpKrYNg8";
        programUid = "eBAyeGv0exc";
        programStageUid = "Zj7UnCAulEk";
        dataElementUid = "qrur9Dvnyt5";
        codeGenerator = new CodeGeneratorImpl();

        eventUid = codeGenerator.generate();

    }

    private void createDummyDataToPost(String orgUnitUid, String programUid,
                                       String programStageUid, String eventUid,
                                       String dataElementUid) {
        eventStore.insert(
                eventUid, null, new Date(), new Date(), null, null,
                EventStatus.ACTIVE, "13.21", "12.21", programUid, programStageUid, orgUnitUid,
                new Date(), new Date(), new Date(), State.TO_POST
        );

        trackedEntityDataValueStore.insert(
                eventUid, new Date(), new Date(), dataElementUid, "user_name", "12", Boolean.FALSE
        );
    }

    // commented out since it is a flaky test that works against a real server.
    //    @Test
    public void successful_response_after_sync_events() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn("android", "Android123").call();
        assertThat(response.isSuccessful()).isTrue();

        response = d2.syncMetaData().call();
        assertThat(response.isSuccessful()).isTrue();


        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid, dataElementUid);

        Call<Response<WebResponse>> call = d2.syncSingleEvents();
        response = call.call();
        assertThat(response.isSuccessful()).isTrue();


    }

    @Test
    public void stub() throws Exception {

    }
}
