package org.hisp.dhis.android.core.category;


import static junit.framework.Assert.assertTrue;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.ResponseValidator;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import retrofit2.Response;

public class CategoryCallEndpointRealShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    @Test
    public void parse_categories() throws Exception {

        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        CategoryCallEndpoint categoryCallEndpoint = provideCategoryCallEndpoint();
        Response<Payload<Category>> responseCategory = categoryCallEndpoint.call();

        assertTrue(responseCategory.isSuccessful());
        assertTrue(hasCategories(responseCategory));

    }

    private CategoryCallEndpoint provideCategoryCallEndpoint() {
        CategoryQuery query = CategoryQuery.defaultQuery();

        CategoryService categoryService = d2.retrofit().create(CategoryService.class);

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

    private boolean hasCategories(Response<Payload<Category>> response) {
        return !response.body().items().isEmpty();
    }

}
