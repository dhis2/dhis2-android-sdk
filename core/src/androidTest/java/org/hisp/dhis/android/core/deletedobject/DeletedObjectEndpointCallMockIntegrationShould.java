package org.hisp.dhis.android.core.deletedobject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.category.CategoryComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionStoreImpl;
import org.hisp.dhis.android.core.category.CategoryStoreImpl;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.IdentifiableStore;
import org.hisp.dhis.android.core.common.responses.AlternativeMetadataMockResponseList;
import org.hisp.dhis.android.core.common.responses.BasicMetadataMockResponseList;
import org.hisp.dhis.android.core.common.responses
        .MetadataWithDeletedCategoryComboOptionsMockResponseList;
import org.hisp.dhis.android.core.common.responses.MetadataWithDeletedProgramStagesMockResponseList;
import org.hisp.dhis.android.core.common.responses.MetadataWithMultipleObjectsMockResponseList;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.dataelement.DataElementStoreImpl;
import org.hisp.dhis.android.core.option.OptionSetStoreImpl;
import org.hisp.dhis.android.core.option.OptionStoreImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStoreImpl;
import org.hisp.dhis.android.core.program.ProgramIndicatorStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleActionStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleVariableStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageSectionStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStoreImpl;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStoreImpl;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStoreImpl;
import org.hisp.dhis.android.core.user.UserStoreImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class DeletedObjectEndpointCallMockIntegrationShould extends AbsStoreTestCase {

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
    public void delete_the_given_deleted_Relationship_types() throws Exception {
        RelationshipTypeStoreImpl relationshipTypeStore = new RelationshipTypeStoreImpl(
                databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("V2kkHafqs82", relationshipTypeStore);
        verifyIfIsPersisted("V2kkHafqs8G", relationshipTypeStore);

        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        verifyIfIsDeleted("V2kkHafqs82", relationshipTypeStore);
        verifyIfIsDeleted("V2kkHafqs8G", relationshipTypeStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_user() throws Exception {
        UserStoreImpl userStore = new UserStoreImpl(databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new BasicMetadataMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("DXyJmlo9rge", userStore);

        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        verifyIfIsDeleted("DXyJmlo9rge", userStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_program_tracked_entity_attributes() throws Exception {
        ProgramTrackedEntityAttributeStoreImpl programTrackedEntityAttributeStore =
                new ProgramTrackedEntityAttributeStoreImpl(
                        databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("CgwB73TbuLC", programTrackedEntityAttributeStore);
        verifyIfIsPersisted("r5BFLb7DBUz", programTrackedEntityAttributeStore);

        dhis2MockServer.enqueueMockResponses(
                new MetadataWithDeletedProgramStagesMockResponseList());
        d2.syncMetaData().call();

        verifyIfIsDeleted("CgwB73TbuLC", programTrackedEntityAttributeStore);
        verifyIfIsDeleted("r5BFLb7DBUz", programTrackedEntityAttributeStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_organisation_unit() throws Exception {
        OrganisationUnitStoreImpl organisationUnitStore = new OrganisationUnitStoreImpl(
                databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("YuQRtpLP102", organisationUnitStore);
        verifyIfIsPersisted("YuQRtpLP10I", organisationUnitStore);

        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        verifyIfIsPersisted("DiszpKrYNg8", organisationUnitStore);
        verifyIfIsDeleted("YuQRtpLP102", organisationUnitStore);
        verifyIfIsDeleted("YuQRtpLP10I", organisationUnitStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_categories() throws Exception {
        CategoryStoreImpl categoryStore = new CategoryStoreImpl(databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("vGs6omsRekv", categoryStore);
        verifyIfIsPersisted("cX5k9anHEHd", categoryStore);

        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        verifyIfIsPersisted("KfdsGBcoiCa", categoryStore);

        verifyIfIsDeleted("vGs6omsRekv", categoryStore);
        verifyIfIsDeleted("cX5k9anHEHd", categoryStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_categoryOptions() throws Exception {
        //given
        CategoryStoreImpl categoryStore = new CategoryStoreImpl(databaseAdapter());
        CategoryOptionStoreImpl categoryOptionStore = new CategoryOptionStoreImpl(
                databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new BasicMetadataMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("TXGfLxZlInA", categoryOptionStore);
        verifyIfIsPersisted("uZUnebiT5DI", categoryOptionStore);

        //when
        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsPersisted("KfdsGBcoiCa", categoryStore);
        verifyIfIsPersisted("TNYQzTHdoxL", categoryOptionStore);

        verifyIfIsDeleted("TXGfLxZlInA", categoryOptionStore);
        verifyIfIsDeleted("uZUnebiT5DI", categoryOptionStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_option_sets() throws Exception {
        //given
        OptionSetStoreImpl optionSetStore = new OptionSetStoreImpl(databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("VQ2lai3OfVG", optionSetStore);
        verifyIfIsPersisted("R3mpvjqJ81H", optionSetStore);

        //when
        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsPersisted("xjA5E9MimMU", optionSetStore);

        verifyIfIsDeleted("VQ2lai3OfVG", optionSetStore);
        verifyIfIsDeleted("R3mpvjqJ81H", optionSetStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_options() throws Exception {
        //given
        OptionSetStoreImpl optionSetStore = new OptionSetStoreImpl(databaseAdapter());
        OptionStoreImpl optionStore = new OptionStoreImpl(databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("Yjte6foKMny", optionStore);
        verifyIfIsPersisted("wfkKVdPBzho", optionStore);

        //when
        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsPersisted("xjA5E9MimMU", optionSetStore);

        verifyIfIsDeleted("Yjte6foKMny", optionStore);
        verifyIfIsDeleted("wfkKVdPBzho", optionStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_category_combo() throws Exception {
        //given
        CategoryComboStoreImpl categoryComboStore = new CategoryComboStoreImpl(databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("p0KPaWEg3cf", categoryComboStore);
        verifyIfIsPersisted("m2jTvAj5kkm", categoryComboStore);

        //when
        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsDeleted("p0KPaWEg3cf", categoryComboStore);
        verifyIfIsDeleted("m2jTvAj5kkm", categoryComboStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_category_option_combo() throws Exception {
        //given
        CategoryOptionComboStoreImpl categoryOptionComboStore = new CategoryOptionComboStoreImpl(
                databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("bRowv6yZOF2", categoryOptionComboStore);
        verifyIfIsPersisted("Gmbgme7z9BF", categoryOptionComboStore);

        //when
        dhis2MockServer.enqueueMockResponses(
                new MetadataWithDeletedCategoryComboOptionsMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsDeleted("bRowv6yZOF2", categoryOptionComboStore);
        verifyIfIsDeleted("Gmbgme7z9BF", categoryOptionComboStore);
    }


    @Test
    @MediumTest
    public void delete_the_given_deleted_data_element() throws Exception {
        //given
        DataElementStoreImpl dataElementStore = new DataElementStoreImpl(databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("Ok9OQpitjQr", dataElementStore);
        verifyIfIsPersisted("sWoqcoByYmD", dataElementStore);

        //when
        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsDeleted("Ok9OQpitjQr", dataElementStore);
        verifyIfIsDeleted("sWoqcoByYmD", dataElementStore);
    }


    @Test
    @MediumTest
    public void delete_the_given_deleted_programs() throws Exception {
        //given
        ProgramStoreImpl programStore = new ProgramStoreImpl(databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("q04UBOqq3rp", programStore);
        verifyIfIsPersisted("kla3mAPgvCH", programStore);

        //when
        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsDeleted("q04UBOqq3rp", programStore);
        verifyIfIsDeleted("kla3mAPgvCH", programStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_program_stages() throws Exception {
        //given
        ProgramStageStoreImpl programStageStore = new ProgramStageStoreImpl(databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("dBwrot7S420", programStageStore);
        verifyIfIsPersisted("A03MvHHogjR", programStageStore);

        //when
        dhis2MockServer.enqueueMockResponses(
                new MetadataWithDeletedProgramStagesMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsDeleted("dBwrot7S420", programStageStore);
        verifyIfIsDeleted("A03MvHHogjR", programStageStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_program_sections() throws Exception {
        //given
        ProgramStageSectionStoreImpl programSectionStore = new ProgramStageSectionStoreImpl(
                databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("d7ZILSbPgYh", programSectionStore);
        verifyIfIsPersisted("OeSqs7pkKqI", programSectionStore);

        //when
        dhis2MockServer.enqueueMockResponses(
                new MetadataWithDeletedProgramStagesMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsDeleted("d7ZILSbPgYh", programSectionStore);
        verifyIfIsDeleted("OeSqs7pkKqI", programSectionStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_tracked_entity_attributes() throws Exception {
        //given
        TrackedEntityAttributeStoreImpl trackedEntityAttributeStore =
                new TrackedEntityAttributeStoreImpl(databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("iESIqZ0R0R0", trackedEntityAttributeStore);
        verifyIfIsPersisted("VqEFza8wbwA", trackedEntityAttributeStore);

        //when
        dhis2MockServer.enqueueMockResponses(
                new MetadataWithDeletedProgramStagesMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsDeleted("iESIqZ0R0R0", trackedEntityAttributeStore);
        verifyIfIsDeleted("VqEFza8wbwA", trackedEntityAttributeStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_program_stage_data_elements() throws Exception {
        //given
        ProgramStageDataElementStoreImpl programStageDataElementStore =
                new ProgramStageDataElementStoreImpl(databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("ztoQtbuXzsI", programStageDataElementStore);
        verifyIfIsPersisted("vdc1saaN2ma", programStageDataElementStore);

        //when
        dhis2MockServer.enqueueMockResponses(
                new MetadataWithDeletedProgramStagesMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsDeleted("ztoQtbuXzsI", programStageDataElementStore);
        verifyIfIsDeleted("vdc1saaN2ma", programStageDataElementStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_program_indicators() throws Exception {
        //given
        ProgramIndicatorStoreImpl programIndicatorStore = new ProgramIndicatorStoreImpl(
                databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("Kswd1r4qWLh", programIndicatorStore);
        verifyIfIsPersisted("hAHF3BEHGjM", programIndicatorStore);

        //when
        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsDeleted("Kswd1r4qWLh", programIndicatorStore);
        verifyIfIsDeleted("hAHF3BEHGjM", programIndicatorStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_program_rules() throws Exception {
        //given
        ProgramRuleStoreImpl programRuleStore = new ProgramRuleStoreImpl(databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("fd3wL1quxGb", programRuleStore);
        verifyIfIsPersisted("OfWLsxH5ylF", programRuleStore);

        //when
        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsDeleted("fd3wL1quxGb", programRuleStore);
        verifyIfIsDeleted("OfWLsxH5ylF", programRuleStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_program_rule_actions() throws Exception {
        //given
        ProgramRuleActionStoreImpl programRuleActionStore = new ProgramRuleActionStoreImpl(
                databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("hLOEPUPseEv", programRuleActionStore);
        verifyIfIsPersisted("actonrw1065", programRuleActionStore);

        //when
        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsDeleted("hLOEPUPseEv", programRuleActionStore);
        verifyIfIsDeleted("actonrw1065", programRuleActionStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_deleted_tracked_entity() throws Exception {
        //given
        TrackedEntityStoreImpl trackedEntityStore = new TrackedEntityStoreImpl(databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new BasicMetadataMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("nEenWmSyUE2", trackedEntityStore);
        verifyIfIsPersisted("nEenWmSyUE3", trackedEntityStore);

        //when
        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();

        //then
        verifyIfIsPersisted("nEenWmSyUEp", trackedEntityStore);
        verifyIfIsDeleted("nEenWmSyUE2", trackedEntityStore);
        verifyIfIsDeleted("nEenWmSyUE3", trackedEntityStore);
    }

    @Test
    @MediumTest
    public void delete_the_given_program_rule_variables() throws Exception {
        ProgramRuleVariableStoreImpl
                programRuleVariableStore = new ProgramRuleVariableStoreImpl(databaseAdapter());
        dhis2MockServer.enqueueMockResponses(new MetadataWithMultipleObjectsMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsPersisted("RycV5uDi66i", programRuleVariableStore);
        verifyIfIsPersisted("zINGRka3g9N", programRuleVariableStore);

        dhis2MockServer.enqueueMockResponses(new AlternativeMetadataMockResponseList());
        d2.syncMetaData().call();
        verifyIfIsDeleted("RycV5uDi66i", programRuleVariableStore);
        verifyIfIsDeleted("zINGRka3g9N", programRuleVariableStore);
    }

    private void verifyIfIsPersisted(String uid, IdentifiableStore store) {
        Boolean isPersisted = store.exists(uid);
        assertThat(isPersisted, is(true));
    }

    private void verifyIfIsDeleted(String uid, IdentifiableStore store) {
        Boolean isPersisted = store.exists(uid);
        assertThat(isPersisted, is(false));
    }
}
