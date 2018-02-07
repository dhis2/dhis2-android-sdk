package org.hisp.dhis.android.core.dataelement;

import android.support.test.filters.LargeTest;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.HandlerFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DataElementEndPointCallRealIntegrationShould extends AbsStoreTestCase {
    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    @Test
    @LargeTest
    public void download_data_element_according_to_default_query() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(response.isSuccessful()).isTrue();

        DataElementFactory dataElementFactory =
                new DataElementFactory(d2.retrofit(), d2.databaseAdapter(),
                        HandlerFactory.createResourceHandler(databaseAdapter()));

        Set<String> uidsSet =
                new HashSet<>(Arrays.asList("FTRrcoaog83", "P+-3jJH5Tu5VC", "FQ2o8UBlcrS"));

        DataElementQuery dataElementQuery = DataElementQuery.Builder.create()
                .withUIds(uidsSet)
                .build();

        response = dataElementFactory.newEndPointCall(dataElementQuery,
                new Date()).call();
        Truth.assertThat(response.isSuccessful()).isTrue();
    }
}