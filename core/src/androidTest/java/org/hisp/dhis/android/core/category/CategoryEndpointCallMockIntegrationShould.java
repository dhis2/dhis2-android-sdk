package org.hisp.dhis.android.core.category;

import static junit.framework.Assert.assertTrue;

import android.support.test.filters.MediumTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class CategoryEndpointCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;

    private CategoryService categoryService;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        Retrofit retrofit = new Retrofit.Builder()
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
        CategoryQuery query = CategoryQuery.builder().paging(true).pageSize(
                CategoryQuery.DEFAULT_PAGE_SIZE).page(1).build();

        ResponseValidator<Category> validator = new ResponseValidator<>();

        CategoryStore store = new CategoryStoreImpl(databaseAdapter());

        CategoryOptionStore categoryOptionStore = new CategoryOptionStoreImpl(databaseAdapter());

        CategoryCategoryOptionLinkStore categoryCategoryOptionLinkStore =
                new CategoryCategoryOptionLinkStoreImpl(databaseAdapter());

        CategoryOptionHandler categoryOptionHandler = new CategoryOptionHandler(
                categoryOptionStore, categoryCategoryOptionLinkStore);

        CategoryHandler handler =
                new CategoryHandler(store, categoryOptionHandler);

        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter());
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);
        Date serverDate = new Date();

        return new CategoryEndpointCall(query, categoryService, validator, handler, resourceHandler,
                databaseAdapter(), serverDate);

    }
}