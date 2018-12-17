package org.hisp.dhis.android.core.category;


import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static junit.framework.Assert.assertFalse;

public class CategoryEndpointCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;
    private APICallExecutor apiCallExecutor;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
        apiCallExecutor = APICallExecutorImpl.create(databaseAdapter());
    }

    //@Test
    public void call_categories_endpoint() throws Exception {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();

        Call<List<Category>> categoryEndpointCall = new CategoryEndpointCallFactory(getGenericCallData(d2), apiCallExecutor).create(
                new HashSet<>(Lists.newArrayList("cX5k9anHEHd")));
        List<Category> categories = categoryEndpointCall.call();

        assertFalse(categories.isEmpty());
    }
}