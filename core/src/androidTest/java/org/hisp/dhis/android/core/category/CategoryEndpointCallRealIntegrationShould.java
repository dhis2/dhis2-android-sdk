package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertFalse;

public class CategoryEndpointCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    @Test
    public void call_categories_endpoint() throws Exception {
        d2.logIn(RealServerMother.user, RealServerMother.password).call();

        Call<List<Category>> categoryEndpointCall = CategoryEndpointCall.FACTORY.create(
                GenericCallData.create(databaseAdapter(), d2.retrofit(), new Date())
        );
        List<Category> categories = categoryEndpointCall.call();

        assertFalse(categories.isEmpty());
    }
}
