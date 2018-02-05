package org.hisp.dhis.android.core.dataelement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import android.support.test.filters.MediumTest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkStore;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkStoreImpl;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkStore;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkStoreImpl;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboHandler;
import org.hisp.dhis.android.core.category.CategoryComboStore;
import org.hisp.dhis.android.core.category.CategoryComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryEndpointCall;
import org.hisp.dhis.android.core.category.CategoryHandler;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryLinkStore;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryLinkStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionComboHandler;
import org.hisp.dhis.android.core.category.CategoryOptionComboStore;
import org.hisp.dhis.android.core.category.CategoryOptionComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionHandler;
import org.hisp.dhis.android.core.category.CategoryOptionStore;
import org.hisp.dhis.android.core.category.CategoryOptionStoreImpl;
import org.hisp.dhis.android.core.category.CategoryQuery;
import org.hisp.dhis.android.core.category.CategoryService;
import org.hisp.dhis.android.core.category.CategoryStore;
import org.hisp.dhis.android.core.category.CategoryStoreImpl;
import org.hisp.dhis.android.core.category.ResponseValidator;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.HandlerFactory;
import org.hisp.dhis.android.core.common.Payload;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class DataElementEndPointCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;
    private DataElementFactory dataElementFactory;
    private CategoryComboStore categoryComboStore;
    private CategoryComboHandler categoryComboHandler;
    private CategoryHandler categoryHandler;


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
                Arrays.asList("FTRrcoaog83", "P+-3jJH5Tu5VC", "FQ2o8UBlcrS")));

        dataElementFactory.newEndPointCall(dataElementQuery, new Date()).call();

        verifyDownloadedDataElements(filename);
    }

    private void verifyDownloadedDataElements(String fileName) throws IOException {
        Payload<DataElement> DataElementPayload = parseDataElementResponse(fileName);

        List<DataElement> downloadedDataElement =
                dataElementFactory.getDataElementStore().queryAll();

        downloadedDataElement = ignoreCategoryCombo(downloadedDataElement);
        assertThat(downloadedDataElement.size(), is(DataElementPayload.items().size()));
        assertThat(downloadedDataElement, is(ignoreCategoryCombo(DataElementPayload.items())));
    }

    private List<DataElement> ignoreCategoryCombo(List<DataElement> downloadedDataElement) {
        List<DataElement> dataElements = new ArrayList<>();
        for(DataElement dataElement:downloadedDataElement){
            dataElements.add(dataElement.toBuilder().categoryCombo(null).build());
        }
        return dataElements;
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