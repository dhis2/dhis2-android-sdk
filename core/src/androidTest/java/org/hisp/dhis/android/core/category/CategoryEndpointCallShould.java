package org.hisp.dhis.android.core.category;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static junit.framework.Assert.assertFalse;

public class CategoryEndpointCallShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();
        dhis2MockServer.shutdown();
    }

    @Test
    public void parse_category_successful() throws Exception {
        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
        Call<List<Category>> callEndpoint =
                CategoryEndpointCall.FACTORY.create(getGenericCallData(d2), new HashSet<>(
                        Lists.newArrayList("vGs6omsRekv", "KfdsGBcoiCa", "cX5k9anHEHd", "x3uo8LqiTBk")));

        dhis2MockServer.enqueueMockResponse("categories/categories.json");

        List<Category> categories = callEndpoint.call();
        assertFalse(categories.isEmpty());
    }
}