package org.hisp.dhis.android.core.relationship;

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

public class RelationshipTypeEndPointCallRealIntegrationShould extends AbsStoreTestCase {
    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    @Test
    @LargeTest
    public void download_RelationshipType_according_to_default_query() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(response.isSuccessful()).isTrue();

        RelationshipTypeFactory relationshipTypeFactory = new RelationshipTypeFactory(
                d2.retrofit(), databaseAdapter(),
                HandlerFactory.createResourceHandler(databaseAdapter()));
        response = relationshipTypeFactory.newEndPointCall(new HashSet<>(
                Arrays.asList("V2kkHafqs8G", "o51cUNONthg")), new Date()).call();
        Truth.assertThat(response.isSuccessful()).isTrue();
    }
}