package org.hisp.dhis.android.core.deletedobject;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hisp.dhis.android.core.common.MockedCalls.AFTER_DELETE_EXPECTED_CATEGORIES;
import static org.hisp.dhis.android.core.common.MockedCalls.AFTER_DELETE_EXPECTED_ORGANISATION_UNIT;
import static org.hisp.dhis.android.core.common.MockedCalls.AFTER_DELETE_EXPECTED_USER;
import static org.hisp.dhis.android.core.common.MockedCalls.CATEGORIES;
import static org.hisp.dhis.android.core.common.MockedCalls.CATEGORY_COMBOS;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_CATEGORIES;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_EMPTY;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_ORGANISATION_UNITS;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_USER;
import static org.hisp.dhis.android.core.common.MockedCalls.ALTERNATIVE_USER;
import static org.hisp.dhis.android.core.common.MockedCalls.MULTIPLE_ORGANISATIONN_UNITS;
import static org.hisp.dhis.android.core.common.MockedCalls.NORMAL_USER;
import static org.hisp.dhis.android.core.common.MockedCalls.OPTION_SETS;
import static org.hisp.dhis.android.core.common.MockedCalls.ORGANISATION_UNITS;
import static org.hisp.dhis.android.core.common.MockedCalls.PROGRAMS;
import static org.hisp.dhis.android.core.common.MockedCalls.SIMPLE_CATEGORIES;
import static org.hisp.dhis.android.core.common.MockedCalls.SYSTEM_INFO;
import static org.hisp.dhis.android.core.common.MockedCalls.TRACKED_ENTITIES;
import static org.hisp.dhis.android.core.common.MockedCalls.USER;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryStoreImpl;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.DownloadedItemsGetter;
import org.hisp.dhis.android.core.common.MockedCalls;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetStoreImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DeletedObjectEndpointCallMockIntegrationShould  extends AbsStoreTestCase {

    final static String[] commonMetadataJsonFiles = new String[]{
            SYSTEM_INFO,
            DELETED_OBJECT_EMPTY, USER,
            DELETED_OBJECT_EMPTY, ORGANISATION_UNITS,
            DELETED_OBJECT_EMPTY, SIMPLE_CATEGORIES,
            DELETED_OBJECT_EMPTY, CATEGORY_COMBOS,
            DELETED_OBJECT_EMPTY, PROGRAMS,
            DELETED_OBJECT_EMPTY, TRACKED_ENTITIES,
            DELETED_OBJECT_EMPTY, OPTION_SETS};

    String[] metadataJsonWithRemovedUser = new String[]{
            SYSTEM_INFO,
            DELETED_OBJECT_USER, ALTERNATIVE_USER,
            DELETED_OBJECT_ORGANISATION_UNITS, ORGANISATION_UNITS,
            DELETED_OBJECT_CATEGORIES, AFTER_DELETE_EXPECTED_CATEGORIES,
            DELETED_OBJECT_EMPTY, CATEGORY_COMBOS,
            DELETED_OBJECT_EMPTY, PROGRAMS,
            DELETED_OBJECT_EMPTY, TRACKED_ENTITIES,
            DELETED_OBJECT_EMPTY, OPTION_SETS};

    public final static String[] commonMetadataWithMultipleObjectsJsonFiles = new String[]{
            SYSTEM_INFO,
            DELETED_OBJECT_EMPTY, ALTERNATIVE_USER,
            DELETED_OBJECT_EMPTY, MULTIPLE_ORGANISATIONN_UNITS,
            DELETED_OBJECT_EMPTY, SIMPLE_CATEGORIES,
            DELETED_OBJECT_EMPTY, CATEGORY_COMBOS,
            DELETED_OBJECT_EMPTY, PROGRAMS,
            DELETED_OBJECT_EMPTY, TRACKED_ENTITIES,
            DELETED_OBJECT_EMPTY, OPTION_SETS};

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_user() throws Exception {
        MockedCalls.givenAMetadataInDatabase(dhis2MockServer);
        d2.syncMetaData().call();

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithRemovedUser);
        d2.syncMetaData().call();

        //verifyDownloadedUsers(AFTER_DELETE_EXPECTED_USER);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_organisation_unit() throws Exception {
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(commonMetadataWithMultipleObjectsJsonFiles);
        d2.syncMetaData().call();

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithRemovedUser);
        d2.syncMetaData().call();

        //verifyDownloadedOrganisationUnits(AFTER_DELETE_EXPECTED_ORGANISATION_UNIT);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_categories() throws Exception {
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(commonMetadataWithMultipleObjectsJsonFiles);
        d2.syncMetaData().call();

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithRemovedUser);
        d2.syncMetaData().call();

        //verifyDownloadedCategories(AFTER_DELETE_EXPECTED_CATEGORIES);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_option_sets() throws Exception {
        MockedCalls.givenAMetadataInDatabase(dhis2MockServer);
        d2.syncMetaData().call();

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithRemovedUser);
        d2.syncMetaData().call();

        verifyIfOptionSetIsDeleted("egT1YqFWsVk");
        verifyIfOptionSetIsDeleted("WckXGsyYola");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_organisation_units() throws Exception {

    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_program() throws Exception {

    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_programs() throws Exception {

    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_constant() throws Exception {

    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_constants() throws Exception {

    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_data_element() throws Exception {

    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_data_elements() throws Exception {

    }

    private void verifyIfOptionSetIsDeleted(String optionSetUid) {
        OptionSetStoreImpl store = new OptionSetStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(optionSetUid);

        assertThat(isPersisted, is(false));
    }

}
