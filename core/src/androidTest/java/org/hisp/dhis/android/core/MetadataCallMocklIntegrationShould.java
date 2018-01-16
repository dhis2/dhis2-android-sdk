package org.hisp.dhis.android.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hisp.dhis.android.core.common.MockedCalls.AFTER_DELETE_EXPECTED_ORGANISATION_UNIT;
import static org.hisp.dhis.android.core.common.MockedCalls.CATEGORY_COMBOS;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_EMPTY;
import static org.hisp.dhis.android.core.common.MockedCalls.NORMAL_USER;
import static org.hisp.dhis.android.core.common.MockedCalls.OPTION_SETS;
import static org.hisp.dhis.android.core.common.MockedCalls.ORGANISATION_UNITS;
import static org.hisp.dhis.android.core.common.MockedCalls.PROGRAMS;
import static org.hisp.dhis.android.core.common.MockedCalls.SIMPLE_CATEGORIES;
import static org.hisp.dhis.android.core.common.MockedCalls.SYSTEM_INFO;
import static org.hisp.dhis.android.core.common.MockedCalls.TRACKED_ENTITIES;
import static org.hisp.dhis.android.core.common.MockedCalls.USER;

import android.support.test.filters.MediumTest;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.DownloadedItemsGetter;
import org.hisp.dhis.android.core.common.MockedCalls;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class MetadataCallMocklIntegrationShould extends AbsStoreTestCase {



    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
    }


    @Test
    @MediumTest
    public void not_delete_nothing_when_the_deletable_object_list_is_empty() throws Exception {
        MockedCalls.givenAMetadataInDatabase(dhis2MockServer);
        d2.syncMetaData().call();

        verifyDownloadedUsers(NORMAL_USER);
        verifyDownloadedOrganisationUnits(AFTER_DELETE_EXPECTED_ORGANISATION_UNIT);
        verifyDownloadedCategories(SIMPLE_CATEGORIES);
        //TODO verify all metadata
        //verifyDownloadedCategoryCombo(".json");
        //verifyDownloadedPrograms("1.json");
        //verifyDownloadedTrackedEntities(".json");
        //verifyDownloadedOptionSets(".json");
    }


    private void verifyDownloadedUsers(String file) throws IOException {
        Payload<User> expectedUserResponse = MockedCalls.parseUserResponse(file);

        List<User> downloadedUsers = DownloadedItemsGetter.getDownloadedUsers(databaseAdapter());

        assertThat(downloadedUsers.size(), is(expectedUserResponse.items().size()));
        assertThat(downloadedUsers, is(expectedUserResponse.items()));
    }


    private void verifyDownloadedOrganisationUnits(String file) throws IOException {
        Payload<OrganisationUnit> expectedOrganisationUnitsResponse =
                MockedCalls.parseOrganisationUnitResponse(file);

        List<OrganisationUnit> downloadedOrganisationUnits =
                DownloadedItemsGetter.getDownloadedOrganisationUnits(d2.databaseAdapter());

        assertThat(downloadedOrganisationUnits.size(),
                is(expectedOrganisationUnitsResponse.items().size()));
        assertThat(downloadedOrganisationUnits, is(expectedOrganisationUnitsResponse.items()));
    }


    private void verifyDownloadedCategories(String file) throws IOException {
        Payload<Category> expectedCategoriesResponse = MockedCalls.parseCategoryResponse(file);

        List<Category> downloadedCategories = DownloadedItemsGetter.getDownloadedCategories(
                databaseAdapter());

        assertThat(downloadedCategories.size(), is(expectedCategoriesResponse.items().size()));
        assertThat(downloadedCategories, is(expectedCategoriesResponse.items()));
    }

    private void verifyDownloadedCategoryCombo(String file) throws IOException {
        Payload<CategoryCombo> expectedCategoryCombosResponse =
                MockedCalls.parseCategoryComboResponse(file);

        List<CategoryCombo> downloadedCategoryCombos =
                DownloadedItemsGetter.getDownloadedCategoryCombos(databaseAdapter());

        assertThat(downloadedCategoryCombos.size(),
                is(expectedCategoryCombosResponse.items().size()));
        assertThat(downloadedCategoryCombos, is(expectedCategoryCombosResponse.items()));
    }


    private void verifyDownloadedPrograms(String file) throws IOException {
        Payload<Program> expectedProgramsResponse = MockedCalls.parseProgramResponse(file);

        List<Program> downloadedPrograms = DownloadedItemsGetter.getDownloadedPrograms(
                databaseAdapter());

        assertThat(downloadedPrograms.size(), is(expectedProgramsResponse.items().size()));
        assertThat(downloadedPrograms, is(expectedProgramsResponse.items()));
    }


    private void verifyDownloadedTrackedEntities(String file) throws IOException {
        Payload<TrackedEntity> expectedTrackedEntitiesResponse =
                MockedCalls.parseTrackedEntityResponse(file);

        List<TrackedEntity> downloadedTrackedEntities =
                DownloadedItemsGetter.getDownloadedTrackedEntities(databaseAdapter());

        assertThat(downloadedTrackedEntities.size(),
                is(expectedTrackedEntitiesResponse.items().size()));
        assertThat(downloadedTrackedEntities, is(expectedTrackedEntitiesResponse.items()));
    }


    private void verifyDownloadedOptionSets(String file) throws IOException {
        Payload<OptionSet> expectedOptionSetsResponse = MockedCalls.parseOptionSetResponse(file);

        List<OptionSet> downloadedOptionSets = DownloadedItemsGetter.getDownloadedOptionSets(
                databaseAdapter());

        assertThat(downloadedOptionSets.size(), is(expectedOptionSetsResponse.items().size()));
        assertThat(downloadedOptionSets, is(expectedOptionSetsResponse.items()));
    }

    private void verifyDownloadedEvents(String file) throws IOException {
        Payload<Event> expectedEventsResponse = MockedCalls.parseEventResponse(file);

        List<Event> downloadedEvents = DownloadedItemsGetter.getDownloadedEvents(databaseAdapter());

        assertThat(downloadedEvents.size(), is(expectedEventsResponse.items().size()));
        assertThat(downloadedEvents, is(expectedEventsResponse.items()));
    }
}