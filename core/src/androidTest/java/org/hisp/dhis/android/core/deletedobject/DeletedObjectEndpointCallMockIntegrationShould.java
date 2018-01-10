package org.hisp.dhis.android.core.deletedobject;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_CATEGORY_OPTION_COMBO;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_DATA_ELEMENTS;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_OPTIONS;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_OPTION_SETS;
import static org.hisp.dhis.android.core.common.MockedCalls.DELETED_OBJECT_PROGRAM_INDICATORS;
import static org.hisp.dhis.android.core.common.MockedCalls.EMPTY_OPTION_SETS;
import static org.hisp.dhis.android.core.common.MockedCalls.EMPTY_PROGRAMS;
import static org.hisp.dhis.android.core.common.MockedCalls.EMPTY_TRACKED_ENTITIES;
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
import static org.hisp.dhis.android.core.common.MockedCalls.EMPTY_CATEGORY_COMBOS;
import static org.hisp.dhis.android.core.common.MockedCalls.EMPTY_ORGANISATION_UNITS;
import static org.hisp.dhis.android.core.common.MockedCalls.MULTIPLE_ORGANISATION_UNITS;
import static org.hisp.dhis.android.core.common.MockedCalls.MULTIPLE_PROGRAMS;
import static org.hisp.dhis.android.core.common.MockedCalls.OPTION_SETS;
import static org.hisp.dhis.android.core.common.MockedCalls.SIMPLE_CATEGORIES;
import static org.hisp.dhis.android.core.common.MockedCalls.SYSTEM_INFO;
import static org.hisp.dhis.android.core.common.MockedCalls.TRACKED_ENTITIES;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.category.CategoryComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionStoreImpl;
import org.hisp.dhis.android.core.category.CategoryStoreImpl;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.MockedCalls;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.dataelement.DataElementStoreImpl;
import org.hisp.dhis.android.core.option.OptionSetStoreImpl;
import org.hisp.dhis.android.core.option.OptionStoreImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStoreImpl;
import org.hisp.dhis.android.core.program.ProgramIndicatorStoreImpl;
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

    String[] metadataJsonWithDeletedObjects = new String[]{
            SYSTEM_INFO,
            DELETED_OBJECT_USER, ALTERNATIVE_USER,
            DELETED_OBJECT_ORGANISATION_UNITS, EMPTY_ORGANISATION_UNITS,
            DELETED_OBJECT_CATEGORIES,
            DELETED_OBJECT_CATEGORY_OPTIONS, EMPTY_CATEGORIES,
            DELETED_OBJECT_CATEGORY_COMBO,
            DELETED_OBJECT_EMPTY, EMPTY_CATEGORY_COMBOS,
            DELETED_OBJECT_PROGRAM_INDICATORS,
            DELETED_OBJECT_DATA_ELEMENTS,
            DELETED_OBJECT_PROGRAMS, EMPTY_PROGRAMS,
            DELETED_OBJECT_TRACKED_ENTITY, EMPTY_TRACKED_ENTITIES,
            DELETED_OBJECT_OPTIONS,
            DELETED_OBJECT_OPTION_SETS, EMPTY_OPTION_SETS};
    String[] metadataJsonWithDeletedCategoryComboOptionsObjects = new String[]{
            SYSTEM_INFO,
            DELETED_OBJECT_USER, ALTERNATIVE_USER,
            DELETED_OBJECT_ORGANISATION_UNITS, EMPTY_ORGANISATION_UNITS,
            DELETED_OBJECT_EMPTY,
            DELETED_OBJECT_EMPTY, EMPTY_CATEGORIES,
            DELETED_OBJECT_EMPTY,
            DELETED_OBJECT_CATEGORY_OPTION_COMBO, EMPTY_CATEGORY_COMBOS,
            DELETED_OBJECT_EMPTY,
            DELETED_OBJECT_EMPTY,
            DELETED_OBJECT_PROGRAMS, EMPTY_PROGRAMS,
            DELETED_OBJECT_TRACKED_ENTITY, EMPTY_TRACKED_ENTITIES,
            DELETED_OBJECT_EMPTY,
            DELETED_OBJECT_OPTION_SETS, EMPTY_OPTION_SETS};

    public final static String[] commonMetadataWithMultipleObjectsJsonFiles = new String[]{
            SYSTEM_INFO,
            DELETED_OBJECT_EMPTY, ALTERNATIVE_USER,
            DELETED_OBJECT_EMPTY, MULTIPLE_ORGANISATION_UNITS,
            DELETED_OBJECT_EMPTY,
            DELETED_OBJECT_EMPTY, SIMPLE_CATEGORIES,
            DELETED_OBJECT_EMPTY,
            DELETED_OBJECT_EMPTY, CATEGORY_COMBOS,
            DELETED_OBJECT_EMPTY,
            DELETED_OBJECT_EMPTY,
            DELETED_OBJECT_EMPTY, MULTIPLE_PROGRAMS,
            DELETED_OBJECT_EMPTY, TRACKED_ENTITIES,
            DELETED_OBJECT_EMPTY,
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

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithDeletedObjects);
        d2.syncMetaData().call();

        verifyIfUserIsDeleted("DXyJmlo9rge");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_organisation_unit() throws Exception {
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(
                commonMetadataWithMultipleObjectsJsonFiles);
        d2.syncMetaData().call();
        verifyIfOrganisationUnitIsPersisted("YuQRtpLP102");
        verifyIfOrganisationUnitIsPersisted("YuQRtpLP10I");

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithDeletedObjects);
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
        verifyIfCategoryIsPersisted("vGs6omsRekv");
        verifyIfCategoryIsPersisted("cX5k9anHEHd");

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithDeletedObjects);
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
        verifyIfCategoryOptionIsPersisted("TXGfLxZlInA");
        verifyIfCategoryOptionIsPersisted("uZUnebiT5DI");

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithDeletedObjects);
        d2.syncMetaData().call();

        verifyIfCategoryIsPersisted("KfdsGBcoiCa");
        verifyIfCategoryOptionIsDeleted("TXGfLxZlInA");
        verifyIfCategoryOptionIsDeleted("uZUnebiT5DI");
        verifyIfCategoryOptionIsPersisted("TNYQzTHdoxL");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_option_sets() throws Exception {
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(commonMetadataWithMultipleObjectsJsonFiles);
        d2.syncMetaData().call();
        verifyIfOptionSetIsPersisted("VQ2lai3OfVG");
        verifyIfOptionSetIsPersisted("R3mpvjqJ81H");

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithDeletedObjects);
        d2.syncMetaData().call();
        verifyIfOptionSetIsPersisted("xjA5E9MimMU");
        verifyIfOptionSetIsDeleted("VQ2lai3OfVG");
        verifyIfOptionSetIsDeleted("R3mpvjqJ81H");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_options() throws Exception {
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(commonMetadataWithMultipleObjectsJsonFiles);
        d2.syncMetaData().call();
        verifyIfOptionsIsPersisted("Yjte6foKMny");
        verifyIfOptionsIsPersisted("wfkKVdPBzho");

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithDeletedObjects);
        d2.syncMetaData().call();
        verifyIfOptionSetIsPersisted("xjA5E9MimMU");
        verifyIfOptionsIsDeleted("Yjte6foKMny");
        verifyIfOptionsIsDeleted("wfkKVdPBzho");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_category_combo() throws Exception {
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(commonMetadataWithMultipleObjectsJsonFiles);
        d2.syncMetaData().call();
        verifyIfCategoryComboIsPersisted("p0KPaWEg3cf");
        verifyIfCategoryComboIsPersisted("m2jTvAj5kkm");

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithDeletedObjects);
        d2.syncMetaData().call();
        verifyIfCategoryComboIsDeleted("p0KPaWEg3cf");
        verifyIfCategoryComboIsDeleted("m2jTvAj5kkm");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_category_option_combo() throws Exception {
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(commonMetadataWithMultipleObjectsJsonFiles);
        d2.syncMetaData().call();
        verifyIfCategoryOptionComboIsPersisted("bRowv6yZOF2");
        verifyIfCategoryOptionComboIsPersisted("Gmbgme7z9BF");

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithDeletedCategoryComboOptionsObjects);
        d2.syncMetaData().call();
        verifyIfCategoryOptionComboIsDeleted("bRowv6yZOF2");
        verifyIfCategoryOptionComboIsDeleted("Gmbgme7z9BF");
    }


    @Test
    @MediumTest
    public void delete_the_given_deleted_data_element() throws Exception {
        //given
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(commonMetadataWithMultipleObjectsJsonFiles);
        d2.syncMetaData().call();
        //when
        verifyIfDataElementIsPersisted("Ok9OQpitjQr");
        verifyIfDataElementIsPersisted("sWoqcoByYmD");
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithDeletedObjects);
        d2.syncMetaData().call();
        //then
        verifyIfDataElementIsDeleted("Ok9OQpitjQr");
        verifyIfDataElementIsDeleted("sWoqcoByYmD");
    }


    @Test
    @MediumTest
    public void delete_the_given_deleted_programs() throws Exception {
        //given
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(commonMetadataWithMultipleObjectsJsonFiles);
        d2.syncMetaData().call();
        verifyIfProgramIsPersisted("q04UBOqq3rp");
        verifyIfProgramIsPersisted("kla3mAPgvCH");
        //when
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithDeletedObjects);
        d2.syncMetaData().call();
        //then
        verifyIfProgramIsDeleted("q04UBOqq3rp");
        verifyIfProgramIsDeleted("kla3mAPgvCH");
    }

    @Test
    @MediumTest
    public void delete_the_given_program_indicators() throws Exception {
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(commonMetadataWithMultipleObjectsJsonFiles);
        d2.syncMetaData().call();
        verifyIfProgramIndicatorIsPersisted("Kswd1r4qWLh");
        verifyIfProgramIndicatorIsPersisted("hAHF3BEHGjM");

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithDeletedObjects);
        d2.syncMetaData().call();
        verifyIfProgramIndicatorIsDeleted("Kswd1r4qWLh");
        verifyIfProgramIndicatorIsDeleted("hAHF3BEHGjM");
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_tracked_entity() throws Exception {
        MockedCalls.givenAMetadataInDatabase(dhis2MockServer);
        d2.syncMetaData().call();
        verifyIfTrackedEntityIsPersisted("nEenWmSyUE2");
        verifyIfTrackedEntityIsPersisted("nEenWmSyUE3");

        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(metadataJsonWithDeletedObjects);
        d2.syncMetaData().call();
        verifyIfTrackedEntityIsPersisted("nEenWmSyUEp");
        verifyIfTrackedEntityIsDeleted("nEenWmSyUE2");
        verifyIfTrackedEntityIsDeleted("nEenWmSyUE3");
    }

    private void verifyIfProgramIndicatorIsPersisted(String uid) {
        ProgramIndicatorStoreImpl store = new ProgramIndicatorStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(uid);

        assertThat(isPersisted, is(true));
    }

    private void verifyIfProgramIndicatorIsDeleted(String uid) {
        ProgramIndicatorStoreImpl store = new ProgramIndicatorStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(uid);

        assertThat(isPersisted, is(false));
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

    private void verifyIfCategoryOptionComboIsDeleted(String uid) {
        CategoryOptionComboStoreImpl store = new CategoryOptionComboStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(uid);

        assertThat(isPersisted, is(false));
    }

    private void verifyIfCategoryOptionComboIsPersisted(String uid) {
        CategoryOptionComboStoreImpl store = new CategoryOptionComboStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(uid);

        assertThat(isPersisted, is(true));
    }

    private void verifyIfDataElementIsPersisted(String uid) {
        DataElementStoreImpl store = new DataElementStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(uid);

        assertThat(isPersisted, is(true));
    }

    private void verifyIfDataElementIsDeleted(String uid) {
        DataElementStoreImpl store = new DataElementStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(uid);

        assertThat(isPersisted, is(false));
    }


    private void verifyIfOptionsIsPersisted(String uid) {
        OptionStoreImpl store = new OptionStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(uid);

        assertThat(isPersisted, is(true));
    }

    private void verifyIfOptionsIsDeleted(String uid) {
        OptionStoreImpl store = new OptionStoreImpl(databaseAdapter());

        Boolean isPersisted = store.exists(uid);

        assertThat(isPersisted, is(false));
    }
}
