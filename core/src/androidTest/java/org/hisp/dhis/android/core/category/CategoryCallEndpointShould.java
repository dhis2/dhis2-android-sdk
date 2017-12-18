package org.hisp.dhis.android.core.category;

import static junit.framework.Assert.assertTrue;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
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

public class CategoryCallEndpointShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;

    private CategoryService categoryService;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        Retrofit retrofit = provideRetrofit();
        categoryService = retrofit.create(CategoryService.class);

    }

    @Test
    public void parse_category_successful() throws Exception {

        CategoryCallEndpoint callEndpoint = provideCategoryCallEndpoint();
        dhis2MockServer.enqueueMockResponse("categories.json");

        Response<Payload<Category>> response = callEndpoint.call();

        assertTrue(response.isSuccessful());
        assertTrue(hasCategories(response));

    }

    private boolean hasCategories(Response<Payload<Category>> response) {
        return !response.body().items().isEmpty();
    }

    private CategoryCallEndpoint provideCategoryCallEndpoint() {
        CategoryQuery query = CategoryQuery.builder().paging(true).pageSize(
                CategoryQuery.DEFAULT_PAGE_SIZE).page(1).build();

        ResponseValidator<Category> validator = new ResponseValidator<>();

        Store<Category> store = new CategoryStoreImpl(databaseAdapter());

        Store<CategoryOption> categoryOptionStore = new CategoryOptionStoreImpl(databaseAdapter());

        CategoryOptionHandler categoryOptionHandler = new CategoryOptionHandler(
                categoryOptionStore);
        Store<CategoryOptionLinkModel> categoryOptionLinkStore = new CategoryOptionLinkStoreImpl(
                databaseAdapter());

        Handler<Category> handler = new CategoryHandler(store, categoryOptionHandler,
                categoryOptionLinkStore);
        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter());
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);
        Date serverDate = new Date();

        return new CategoryCallEndpoint(query, categoryService, validator, handler, resourceHandler,
                databaseAdapter(), serverDate);

    }

    @NonNull
    private Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(dhis2MockServer.getBaseEndpoint())
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .addConverterFactory(FilterConverterFactory.create())
                .addConverterFactory(FieldsConverterFactory.create())
                .build();
    }

    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
        clearTablesData();
    }

    private void clearTablesData() {
        databaseAdapter().delete(CategoryModel.TABLE);
    }

}