/*
package org.hisp.dhis.android.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.DownloadedItemsGetter;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.responses.BasicMetadataMockResponseList;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.user.User;
import org.junit.Before;

import java.io.IOException;
import java.util.List;

public class MetadataCallMockIntegrationShould extends AbsStoreTestCase {

    public static final String SIMPLE_CATEGORIES = "deletedobject/simple_categories.json";
    public static final String AFTER_DELETE_EXPECTED_ORGANISATION_UNIT =
            "deletedobject/expected_not_deleted_organisationUnit.json";
    public static final String NORMAL_USER =
            "deletedobject/expected_normal_user.json";

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
    }


    //TODO verify all metadata
    //@Test
    //@MediumTest
    public void not_delete_nothing_when_the_deletable_object_list_is_empty() throws Exception {
        dhis2MockServer.enqueueMockResponses(new BasicMetadataMockResponseList());
        d2.syncMetaData().call();

        verifyDownloadedUsers(NORMAL_USER);
        verifyDownloadedOrganisationUnits(AFTER_DELETE_EXPECTED_ORGANISATION_UNIT);
        verifyDownloadedCategories(SIMPLE_CATEGORIES);
        //verifyDownloadedCategoryCombo(".json");
        //verifyDownloadedPrograms("1.json");
        //verifyDownloadedTrackedEntities(".json");
        //verifyDownloadedOptionSets(".json");
    }


    private void verifyDownloadedUsers(String file) throws IOException {
        Payload<User> expectedUserResponse = parseUserResponse(file);

        List<User> downloadedUsers = DownloadedItemsGetter.getDownloadedUsers(databaseAdapter());

        assertThat(downloadedUsers.size(), is(expectedUserResponse.items().size()));
        assertThat(downloadedUsers, is(expectedUserResponse.items()));
    }


    private void verifyDownloadedOrganisationUnits(String file) throws IOException {
        Payload<OrganisationUnit> expectedOrganisationUnitsResponse =
                parseOrganisationUnitResponse(file);

        List<OrganisationUnit> downloadedOrganisationUnits =
                DownloadedItemsGetter.getDownloadedOrganisationUnits(d2.databaseAdapter());

        assertThat(downloadedOrganisationUnits.size(),
                is(expectedOrganisationUnitsResponse.items().size()));
        assertThat(downloadedOrganisationUnits, is(expectedOrganisationUnitsResponse.items()));
    }


    private void verifyDownloadedCategories(String file) throws IOException {
        Payload<Category> expectedCategoriesResponse = parseCategoryResponse(file);

        List<Category> downloadedCategories = DownloadedItemsGetter.getDownloadedCategories(
                databaseAdapter());

        assertThat(downloadedCategories.size(), is(expectedCategoriesResponse.items().size()));
        assertThat(downloadedCategories, is(expectedCategoriesResponse.items()));
    }

    private void verifyDownloadedCategoryCombo(String file) throws IOException {
        Payload<CategoryCombo> expectedCategoryCombosResponse =
                parseCategoryComboResponse(file);

        List<CategoryCombo> downloadedCategoryCombos =
                DownloadedItemsGetter.getDownloadedCategoryCombos(databaseAdapter());

        assertThat(downloadedCategoryCombos.size(),
                is(expectedCategoryCombosResponse.items().size()));
        assertThat(downloadedCategoryCombos, is(expectedCategoryCombosResponse.items()));
    }


    private void verifyDownloadedPrograms(String file) throws IOException {
        Payload<Program> expectedProgramsResponse = parseProgramResponse(file);

        List<Program> downloadedPrograms = DownloadedItemsGetter.getDownloadedPrograms(
                databaseAdapter());

        assertThat(downloadedPrograms.size(), is(expectedProgramsResponse.items().size()));
        assertThat(downloadedPrograms, is(expectedProgramsResponse.items()));
    }


    private void verifyDownloadedTrackedEntities(String file) throws IOException {
        Payload<TrackedEntity> expectedTrackedEntitiesResponse =
                parseTrackedEntityResponse(file);

        List<TrackedEntity> downloadedTrackedEntities =
                DownloadedItemsGetter.getDownloadedTrackedEntities(databaseAdapter());

        assertThat(downloadedTrackedEntities.size(),
                is(expectedTrackedEntitiesResponse.items().size()));
        assertThat(downloadedTrackedEntities, is(expectedTrackedEntitiesResponse.items()));
    }


    private void verifyDownloadedOptionSets(String file) throws IOException {
        Payload<OptionSet> expectedOptionSetsResponse = parseOptionSetResponse(file);

        List<OptionSet> downloadedOptionSets = DownloadedItemsGetter.getDownloadedOptionSets(
                databaseAdapter());

        assertThat(downloadedOptionSets.size(), is(expectedOptionSetsResponse.items().size()));
        assertThat(downloadedOptionSets, is(expectedOptionSetsResponse.items()));
    }

    private void verifyDownloadedEvents(String file) throws IOException {
        Payload<Event> expectedEventsResponse = parseEventResponse(file);

        List<Event> downloadedEvents = DownloadedItemsGetter.getDownloadedEvents(databaseAdapter());

        assertThat(downloadedEvents.size(), is(expectedEventsResponse.items().size()));
        assertThat(downloadedEvents, is(expectedEventsResponse.items()));
    }


    public static Payload<User> parseUserResponse(String file) throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<User>>() {
                });
    }

    public static Payload<OrganisationUnit> parseOrganisationUnitResponse(String file)
            throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<OrganisationUnit>>() {
                });
    }

    public static Payload<Category> parseCategoryResponse(String file) throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<Category>>() {
                });
    }

    public static Payload<CategoryCombo> parseCategoryComboResponse(String file)
            throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<CategoryCombo>>() {
                });
    }

    public static Payload<Program> parseProgramResponse(String file) throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<Program>>() {
                });
    }

    public static Payload<TrackedEntity> parseTrackedEntityResponse(String file)
            throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<TrackedEntity>>() {
                });
    }

    public static Payload<OptionSet> parseOptionSetResponse(String file) throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<OptionSet>>() {
                });
    }

    public static Payload<Event> parseEventResponse(String file) throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<Event>>() {
                });
    }
}*/
