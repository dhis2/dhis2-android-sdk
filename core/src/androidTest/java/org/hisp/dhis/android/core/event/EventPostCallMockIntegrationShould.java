package org.hisp.dhis.android.core.event;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class EventPostCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void return_success_if_no_events() throws Exception {
        givenAMetadataInDatabase();

        WebResponse response = d2.eventModule().events.upload().call();

        assertThat(response.httpStatusCode()).isEqualTo(200);
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMetadataResponses();
        d2.syncMetaData().call();
    }
}