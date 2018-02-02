package org.hisp.dhis.android.core.dataelement;

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
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class DataElementEndPointCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;
    private DataElementFactory dataElementFactory;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        dataElementFactory = new DataElementFactory(
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
        String filename = "data_elements.json";

        dhis2MockServer.enqueueMockResponse(filename);

        DataElementQuery dataElementQuery = new DataElementQuery(new HashSet<>(
                Arrays.asList("FTRrcoaog83", "P3jJH5Tu5VC", "FQ2o8UBlcrS")));

        dataElementFactory.newEndPointCall(dataElementQuery, new Date()).call();

        verifyDownloadedRelationshipTypes(filename);
    }


    private void verifyDownloadedRelationshipTypes(String fileName) throws IOException {
        Payload<DataElement> DataElementPayload = parseDataElementResponse(fileName);

        List<DataElement> downloadedDataElement =
                dataElementFactory.getDataElementStore().queryAll();

        assertThat(downloadedDataElement.size(), is(DataElementPayload.items().size()));
        assertThat(downloadedDataElement, is(DataElementPayload.items()));
    }

    private Payload<DataElement> parseDataElementResponse(String fileName)
            throws IOException {
        String expectedDataElementResponseJson = new AssetsFileReader().getStringFromFile(
                fileName);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());
        return objectMapper.readValue(expectedDataElementResponseJson,
                new TypeReference<Payload<DataElement>>() {
                });
    }
}