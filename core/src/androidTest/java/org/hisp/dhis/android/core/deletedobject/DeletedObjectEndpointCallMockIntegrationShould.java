package org.hisp.dhis.android.core.deletedobject;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hisp.dhis.android.core.common.MockedCalls.AFTER_DELETE_EXPECTED_CATEGORIES;
import static org.hisp.dhis.android.core.common.MockedCalls.AFTER_PROGRAMS;
import static org.hisp.dhis.android.core.common.MockedCalls.AFTER_TRACKED_ENTITIES;
import static org.hisp.dhis.android.core.common.MockedCalls.CATEGORY_COMBOS;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_CATEGORIES;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_CATEGORY_COMBO;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_CATEGORY_OPTIONS;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_EMPTY;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_ORGANISATION_UNITS;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_PROGRAMS;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_TRACKED_ENTITY;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_USER;
import static org.hisp.dhis.android.core.common.MockedCalls.ALTERNATIVE_USER;
import static org.hisp.dhis.android.core.common.MockedCalls.EMPTY_CATEGORIES;
import static org.hisp.dhis.android.core.common.MockedCalls.MULTIPLE_ORGANISATION_UNITS;
import static org.hisp.dhis.android.core.common.MockedCalls.OPTION_SETS;
import static org.hisp.dhis.android.core.common.MockedCalls.ORGANISATION_UNITS;
import static org.hisp.dhis.android.core.common.MockedCalls.PROGRAMS;
import static org.hisp.dhis.android.core.common.MockedCalls.SIMPLE_CATEGORIES;
import static org.hisp.dhis.android.core.common.MockedCalls.SYSTEM_INFO;
import static org.hisp.dhis.android.core.common.MockedCalls.TRACKED_ENTITIES;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.category.CategoryComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionStoreImpl;
import org.hisp.dhis.android.core.category.CategoryStoreImpl;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.MockedCalls;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.option.OptionSetStoreImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStoreImpl;
import org.hisp.dhis.android.core.user.UserStoreImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class DeletedObjectEndpointCallMockIntegrationShould extends AbsStoreTestCase {

    String[] metadataJsonWithRemovedUser = new String[]{
            SYSTEM_INFO,
            DELETED_OBJECT_USER, ALTERNATIVE_USER,
            DELETED_OBJECT_ORGANISATION_UNITS, ORGANISATION_UNITS,
            DELETED_OBJECT_CATEGORIES,
            DELETED_OBJECT_EMPTY, AFTER_DELETE_EXPECTED_CATEGORIES,
            DELETED_OBJECT_CATEGORY_COMBO, CATEGORY_COMBOS,
            DELETED_OBJECT_PROGRAMS, AFTER_PROGRAMS,
            DELETED_OBJECT_TRACKED_ENTITY, AFTER_TRACKED_ENTITIES,
            DELETED_OBJECT_EMPTY, OPTION_SETS};

    String[] metadataJsonWithRemovedCategoryOption = new String[]{
            SYSTEM_INFO,
            DELETED_OBJECT_USER, ALTERNATIVE_USER,
            DELETED_OBJECT_ORGANISATION_UNITS, ORGANISATION_UNITS,
            DELETED_OBJECT_CATEGORIES,
            DELETED_OBJECT_CATEGORY_OPTIONS, EMPTY_CATEGORIES,
            DELETED_OBJECT_CATEGORY_COMBO, CATEGORY_COMBOS,
            DELETED_OBJECT_PROGRAMS, AFTER_PROGRAMS,
            DELETED_OBJECT_TRACKED_ENTITY, AFTER_TRACKED_ENTITIES,
            DELETED_OBJECT_EMPTY, OPTION_SETS};

    public final static String[] commonMetadataWithMultipleObjectsJsonFiles = new String[]{
            SYSTEM_INFO,
            DELETED_OBJECT_EMPTY, ALTERNATIVE_USER,
            DELETED_OBJECT_EMPTY, MULTIPLE_ORGANISATION_UNITS,
            DELETED_OBJECT_EMPTY,
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

        verifyIfUserIsDeleted("DXyJmlo9rge");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_organisation_unit() throws Exception {
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(
                commonMetadataWithMultipleObjectsJsonFiles);
        d2.syncMetaData().call();

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithRemovedUser);
        d2.syncMetaData().call();

        verifyIfOrganisationUnitIsPersisted("DiszpKrYNg8");
        verifyIfOrganisationUnitIsDeleted("YuQRtpLP102");
        verifyIfOrganisationUnitIsDeleted("YuQRtpLP10I");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_categories() throws Exception {
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(
                commonMetadataWithMultipleObjectsJsonFiles);
        d2.syncMetaData().call();

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithRemovedUser);
        d2.syncMetaData().call();

        verifyIfCategoryIsPersisted("KfdsGBcoiCa");
        verifyIfCategoryIsDeleted("vGs6omsRekv");
        verifyIfCategoryIsDeleted("cX5k9anHEHd");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_categoryOptions() throws Exception {
        MockedCalls.givenAMetadataInDatabase(dhis2MockServer);
        d2.syncMetaData().call();

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithRemovedCategoryOption);
        d2.syncMetaData().call();

        verifyIfCategoryIsPersisted("KfdsGBcoiCa");
        verifyIfCategoryOptionIsDeleted("TXGfLxZlInA");
        verifyIfCategoryOptionIsDeleted("uZUnebiT5DI");
        verifyIfCategoryOptionIsPersisted("TNYQzTHdoxL");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_option_sets() throws Exception {
        MockedCalls.givenAMetadataInDatabase(dhis2MockServer);
        d2.syncMetaData().call();

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithRemovedUser);
        d2.syncMetaData().call();
        verifyIfOptionSetIsPersisted("xjA5E9MimMU");
        verifyIfOptionSetIsDeleted("egT1YqFWsVk");
        verifyIfOptionSetIsDeleted("WckXGsyYola");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_category_combo() throws Exception {
        MockedCalls.givenAMetadataInDatabase(dhis2MockServer);
        d2.syncMetaData().call();

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithRemovedUser);
        d2.syncMetaData().call();
        verifyIfCategoryComboIsPersisted("p0KPaWEg3cf");
        verifyIfCategoryComboIsDeleted("TXGfLxZlInA");
        verifyIfCategoryComboIsDeleted("TNYQzTHdoxL");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_programs() throws Exception {
        MockedCalls.givenAMetadataInDatabase(dhis2MockServer);
        d2.syncMetaData().call();

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithRemovedUser);
        d2.syncMetaData().call();
        verifyIfProgramIsPersisted("lxAQ7Zs9VYR");
        verifyIfProgramIsDeleted("TXGfLxZlInA");
        verifyIfProgramIsDeleted("TNYQzTHdoxL");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_tracked_entity() throws Exception {
        MockedCalls.givenAMetadataInDatabase(dhis2MockServer);
        d2.syncMetaData().call();

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithRemovedUser);
        d2.syncMetaData().call();
        verifyIfTrackedEntityIsPersisted("nEenWmSyUEp");
        verifyIfTrackedEntityIsDeleted("nEenWmSyUE2");
        verifyIfTrackedEntityIsDeleted("nEenWmSyUE3");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_constants() throws Exception {
        MockedCalls.givenAMetadataInDatabase(dhis2MockServer);
        d2.syncMetaData().call();

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithRemovedUser);
        d2.syncMetaData().call();
        verifyIfTrackedEntityIsPersisted("nEenWmSyUEp");
        verifyIfTrackedEntityIsDeleted("nEenWmSyUE2");
        verifyIfTrackedEntityIsDeleted("nEenWmSyUE3");
    }

    private void verifyIfCategoryOptionIsPersisted(String categoryOptionUid) {
        CategoryOptionStoreImpl store = new CategoryOptionStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(categoryOptionUid);

        assertThat(isPersisted, is(true));

    }

    private void verifyIfCategoryOptionIsDeleted(String categoryOptionUid) {
        CategoryOptionStoreImpl store = new CategoryOptionStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(categoryOptionUid);

        assertThat(isPersisted, is(false));
    }

    private void verifyIfTrackedEntityIsPersisted(String trackedEntityUId) {
        TrackedEntityStoreImpl store = new TrackedEntityStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(trackedEntityUId);

        assertThat(isPersisted, is(true));
    }

    private void verifyIfTrackedEntityIsDeleted(String trackedEntityUId) {
        TrackedEntityStoreImpl store = new TrackedEntityStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(trackedEntityUId);

        assertThat(isPersisted, is(false));
    }

    private void verifyIfProgramIsPersisted(String programUId) {
        ProgramStoreImpl store = new ProgramStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(programUId);

        assertThat(isPersisted, is(true));
    }

    private void verifyIfProgramIsDeleted(String programUId) {
        ProgramStoreImpl store = new ProgramStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(programUId);

        assertThat(isPersisted, is(false));
    }

    private void verifyIfCategoryComboIsPersisted(String categoryComboUId) {
        CategoryComboStoreImpl store = new CategoryComboStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(categoryComboUId);

        assertThat(isPersisted, is(true));
    }

    private void verifyIfCategoryComboIsDeleted(String categoryComboUId) {
        CategoryComboStoreImpl store = new CategoryComboStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(categoryComboUId);

        assertThat(isPersisted, is(false));
    }

    private void verifyIfCategoryIsPersisted(String categoryUId) {
        CategoryStoreImpl store = new CategoryStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(categoryUId);

        assertThat(isPersisted, is(true));
    }

    private void verifyIfCategoryIsDeleted(String categoryUId) {
        CategoryStoreImpl store = new CategoryStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(categoryUId);

        assertThat(isPersisted, is(false));
    }

    private void verifyIfOrganisationUnitIsDeleted(String organisationUnitUId) {
        OrganisationUnitStoreImpl store = new OrganisationUnitStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(organisationUnitUId);

        assertThat(isPersisted, is(false));
    }

    private void verifyIfOrganisationUnitIsPersisted(String organisationUnitUid) {
        OrganisationUnitStoreImpl store = new OrganisationUnitStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(organisationUnitUid);

        assertThat(isPersisted, is(true));
    }


    private void verifyIfUserIsDeleted(String userUId) {
        UserStoreImpl store = new UserStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(userUId);

        assertThat(isPersisted, is(false));
    }

    private void verifyIfOptionSetIsDeleted(String optionSetUid) {
        OptionSetStoreImpl store = new OptionSetStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(optionSetUid);

        assertThat(isPersisted, is(false));
    }

    private void verifyIfOptionSetIsPersisted(String optionSetUid) {
        OptionSetStoreImpl store = new OptionSetStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(optionSetUid);

        assertThat(isPersisted, is(true));
    }
}
