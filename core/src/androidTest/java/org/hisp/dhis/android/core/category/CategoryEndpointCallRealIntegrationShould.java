package org.hisp.dhis.android.core.category;


import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import retrofit2.Response;

import static junit.framework.Assert.assertTrue;

public class CategoryEndpointCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    //@Test
    public void call_categories_endpoint() throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Call<Response<Payload<Category>>> categoryEndpointCall = CategoryEndpointCall.FACTORY.create(
                GenericCallData.create(databaseAdapter(), d2.retrofit(), new Date())
        );
        Response<Payload<Category>> responseCategory = categoryEndpointCall.call();

        assertTrue(responseCategory.isSuccessful());
        assertTrue(hasCategories(responseCategory));
    }

    private boolean hasCategories(Response<Payload<Category>> response) {
        return !response.body().items().isEmpty();
    }

}
