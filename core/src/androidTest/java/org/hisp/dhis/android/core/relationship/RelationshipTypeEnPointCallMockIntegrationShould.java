package org.hisp.dhis.android.core.relationship;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import android.support.test.filters.MediumTest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.HandlerFactory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class RelationshipTypeEnPointCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;
    private RelationshipTypeFactory relationshipTypeFactory;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        relationshipTypeFactory = new RelationshipTypeFactory(
                d2.retrofit(), databaseAdapter(),
                HandlerFactory.createResourceHandler(databaseAdapter()));
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();
        dhis2MockServer.shutdown();
    }

    @Test
    @MediumTest
    public void download_RelationShipTypes_according_to_default_query() throws Exception {
        String filename = "relationship_types.json";

        RelationshipTypeFactory relationshipTypeFactory = new RelationshipTypeFactory(
                d2.retrofit(), databaseAdapter(),
                HandlerFactory.createResourceHandler(databaseAdapter()));

        dhis2MockServer.enqueueMockResponse(filename);

        relationshipTypeFactory.newEndPointCall(new HashSet<>(
                Arrays.asList("V2kkHafqs8G", "o51cUNONthg")), new Date()).call();

        verifyDownloadedRelationshipTypes(filename);
    }


    private void verifyDownloadedRelationshipTypes(String fileName) throws IOException {
        Payload<RelationshipType> relationshipTypePayload = parseRelationshipTypeResponse(fileName);

        List<RelationshipType> downloadedRelationshipType =
                relationshipTypeFactory.getRelationshipTypeStore().queryAll();

        assertThat(downloadedRelationshipType.size(), is(relationshipTypePayload.items().size()));
        assertThat(downloadedRelationshipType, is(relationshipTypePayload.items()));
    }

    private Payload<RelationshipType> parseRelationshipTypeResponse(String fileName)
            throws IOException {
        String expectedRelationshipTypeResponseJson = new AssetsFileReader().getStringFromFile(
                fileName);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());
        return objectMapper.readValue(expectedRelationshipTypeResponseJson,
                new TypeReference<Payload<RelationshipType>>() {
                });
    }
}
