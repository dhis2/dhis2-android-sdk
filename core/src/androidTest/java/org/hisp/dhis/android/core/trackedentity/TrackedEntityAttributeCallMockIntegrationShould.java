package org.hisp.dhis.android.core.trackedentity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_IS_TRANSLATION_ON;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_TRANSLATION_LOCALE;

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
import java.util.Set;


public class TrackedEntityAttributeCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private TrackedEntityAttributeFactory trackedEntityAttributeFactory;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        trackedEntityAttributeFactory =
                new TrackedEntityAttributeFactory(d2.retrofit(), databaseAdapter(),
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
    public void download_TrackedEntityAttributes_according_to_default_query() throws Exception {

        dhis2MockServer.enqueueMockResponse("tracked_entity_attributes.json");

        Set<String> uIds =
                new HashSet<>(Arrays.asList("VqEFza8wbwA", "spFvx9FndA4", "gHGyrwKPzej"));

        TrackedEntityAttributeQuery trackedEntityAttributeQuery =
                TrackedEntityAttributeQuery.defaultQuery(uIds, DEFAULT_IS_TRANSLATION_ON,
                        DEFAULT_TRANSLATION_LOCALE);

        trackedEntityAttributeFactory.newEndPointCall(trackedEntityAttributeQuery, new Date())
                .call();

        verifyDownloadedTrackedEntityAttributes("tracked_entity_attributes.json");
    }

    private void verifyDownloadedTrackedEntityAttributes(String file) throws IOException {
        Payload<TrackedEntityAttribute> trackedEntityAttributePayload =
                parseTrackedEntityAttributeResponse(file);

        List<TrackedEntityAttribute> trackedEntityAttributesDownloaded =
                getDownloadedTrackedEntityAttributes();

        assertThat(trackedEntityAttributesDownloaded.size(),
                is(trackedEntityAttributePayload.items().size()));
        assertThat(trackedEntityAttributesDownloaded, is(trackedEntityAttributePayload.items()));
    }

    private List<TrackedEntityAttribute> getDownloadedTrackedEntityAttributes() {
        TrackedEntityAttributeStore trackedEntityAttributeStore =
                trackedEntityAttributeFactory.getTrackedEntityAttributeStore();

        List<TrackedEntityAttribute> downloadedTrackedEntityAttributes =
                trackedEntityAttributeStore.queryAll();

        return downloadedTrackedEntityAttributes;
    }

    private Payload<TrackedEntityAttribute> parseTrackedEntityAttributeResponse(String file)
            throws IOException {
        String expectedTrackedEntityAttributeResponseJson =
                new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedTrackedEntityAttributeResponseJson,
                new TypeReference<Payload<TrackedEntityAttribute>>() {
                });
    }
}
