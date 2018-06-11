package org.hisp.dhis.android.core.category;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static junit.framework.Assert.assertFalse;

public class CategoryEndpointCallShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;

    private Retrofit retrofit;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        retrofit = new Retrofit.Builder()
                .baseUrl(dhis2MockServer.getBaseEndpoint())
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .addConverterFactory(FilterConverterFactory.create())
                .addConverterFactory(FieldsConverterFactory.create())
                .build();
    }

    @Override
    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test
    public void parse_category_successful() throws Exception {

        GenericCallData data = GenericCallData.create(databaseAdapter(), retrofit, new Date());
        Call<List<Category>> callEndpoint = CategoryEndpointCall.FACTORY.create(data);
        dhis2MockServer.enqueueMockResponse("categories.json");

        List<Category> categories = callEndpoint.call();
        assertFalse(categories.isEmpty());
    }
}