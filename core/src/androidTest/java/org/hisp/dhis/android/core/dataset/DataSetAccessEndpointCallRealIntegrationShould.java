package org.hisp.dhis.android.core.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class DataSetAccessEndpointCallRealIntegrationShould extends AbsStoreTestCase {
    private D2 d2;
    private DataSetAccessEndpointCall dataSetAccessCall;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create("https://play.dhis2.org/dev/api/", databaseAdapter());
        dataSetAccessCall = createCall();
    }

    private DataSetAccessEndpointCall createCall() {
        ResourceHandler resourceHandler =
                new ResourceHandler(new ResourceStoreImpl(databaseAdapter()));
        GenericCallData data = GenericCallData.create(databaseAdapter(), resourceHandler, d2.retrofit());

        return DataSetAccessEndpointCall.FACTORY.create(data);
    }

    @Test
    public void download_data_sets() throws Exception {
        if (!d2.isUserLoggedIn().call()) {
            retrofit2.Response loginResponse = d2.logIn("android", "Android123").call();
            assertThat(loginResponse.isSuccessful()).isTrue();
        }

        retrofit2.Response dataSetResponse = dataSetAccessCall.call();
        assertThat(dataSetResponse.isSuccessful()).isTrue();
    }
}
