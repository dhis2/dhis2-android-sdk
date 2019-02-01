package org.hisp.dhis.android.core.trackedentity.glass;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.arch.api.responses.HttpMessageResponse;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class BreakTheGlassMockIntegrationShould extends AbsStoreTestCase {

    private D2 d2;
    private Dhis2MockServer dhis2MockServer;
    private APICallExecutor executor;

    private TrackedEntityInstanceService trackedEntityInstanceService;

    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());
        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        executor = APICallExecutorImpl.create(d2.databaseAdapter());

        trackedEntityInstanceService = d2.retrofit().create(TrackedEntityInstanceService.class);
    }

    @Test
    public void parse_successful_break_the_glass_response() throws Exception {

        dhis2MockServer.enqueueMockResponse("trackedentity/glass/break_glass_successful.json");

        HttpMessageResponse response = executor.executeObjectCall(
                trackedEntityInstanceService.breakGlass("tei", "program", "reason"));

        assertThat(response.httpStatus()).isEqualTo("OK");
        assertThat(response.httpStatusCode()).isEqualTo(200);
        assertThat(response.status()).isEqualTo("OK");
        assertThat(response.message()).isEqualTo("Temporary Ownership granted");
    }


}
