package org.hisp.dhis.android.core.category;

import static junit.framework.Assert.assertTrue;

import android.support.test.filters.MediumTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.common.CategoryCallFactory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class CategoryEndpointCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;

    private CategoryService categoryService;

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

        categoryService = retrofit.create(CategoryService.class);

    }

    @Override
    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test
    @MediumTest
    public void parse_category_successful() throws Exception {

        CategoryEndpointCall callEndpoint = provideCategoryEndpointCall();
        dhis2MockServer.enqueueMockResponse("categories.json");

        Response<Payload<Category>> response = callEndpoint.call();

        assertTrue(response.isSuccessful());
        assertTrue(hasCategories(response));

    }

    private boolean hasCategories(Response<Payload<Category>> response) {
        return !response.body().items().isEmpty();
    }

    private CategoryEndpointCall provideCategoryEndpointCall() {
        return CategoryCallFactory.create(retrofit,databaseAdapter());
    }
}