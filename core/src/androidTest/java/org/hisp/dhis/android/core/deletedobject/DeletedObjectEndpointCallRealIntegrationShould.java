package org.hisp.dhis.android.core.deletedobject;


import static junit.framework.Assert.assertTrue;

import android.support.test.filters.LargeTest;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.HandlerFactory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageSection;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.user.User;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Response;

public class DeletedObjectEndpointCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;
    private DeletedObjectFactory deletedObjectFactory;

    private ResourceStore resourceStore;
    private Date currentDate;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());

        deletedObjectFactory =
                new DeletedObjectFactory(d2.retrofit(), databaseAdapter(),
                        HandlerFactory.createResourceHandler(databaseAdapter()));

        resourceStore = new ResourceStoreImpl(databaseAdapter());
        currentDate = new Date();
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_program_deleted_object_is_called() throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                Program.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_PROGRAM);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_relationship_type_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                RelationshipType.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_RELATIONSHIP_TYPE);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_tracked_entity_attribute_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                TrackedEntityAttribute.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_TRACKED_ENTITY_ATTRIBUTE);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_tracked_entity_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                TrackedEntity.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_TRACKED_ENTITY);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_program_stage_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                ProgramStage.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_PROGRAM_STAGE);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_program_stage_data_element_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                ProgramStageDataElement.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_PROGRAM_STAGE_DATA_ELEMENT);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_program_stage_section_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                ProgramStageSection.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_PROGRAM_STAGE_SECTION);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_program_rule_deleted_object_is_called() throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                ProgramRule.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_PROGRAM_RULE);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_program_rule_action_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                ProgramRuleAction.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_PROGRAM_RULE_ACTION);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_program_rule_variable_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                ProgramRuleVariable.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_PROGRAM_RULE_VARIABLE);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_program_indicator_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                ProgramIndicator.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_PROGRAM_INDICATOR);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_option_deleted_object_is_called() throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                Option.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_OPTION);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_data_element_deleted_object_is_called() throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                DataElement.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_DATA_ELEMENT);
    }

    @Test
    @LargeTest
    public void
    persist_in_resources_when_program_tracked_entity_attribute_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                ProgramTrackedEntityAttribute.class, currentDate).call();

        verifyResponseAndResources(response,
                ResourceModel.Type.DELETED_PROGRAM_TRACKED_ENTITY_ATTRIBUTE);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_option_set_deleted_object_is_called() throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                OptionSet.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_OPTION_SET);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_organisation_units_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                OrganisationUnit.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_ORGANISATION_UNIT);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_user_deleted_object_is_called() throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                User.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_USER);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_category_combo_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                CategoryCombo.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_CATEGORY_COMBO);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_category_option_combo_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                CategoryOptionCombo.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_CATEGORY_OPTION_COMBO);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_category_option_deleted_object_is_called()
            throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                CategoryOption.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_CATEGORY_OPTION);
    }

    @Test
    @LargeTest
    public void persist_in_resources_when_category_deleted_object_is_called() throws Exception {
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        Response<Payload<DeletedObject>> response = deletedObjectFactory.newEndPointCall(
                Category.class, currentDate).call();

        verifyResponseAndResources(response, ResourceModel.Type.DELETED_CATEGORY);
    }

    private void verifyResponseAndResources(Response response, ResourceModel.Type type)
            throws ParseException {

        assertTrue(response.isSuccessful());
        String lastUpdated = resourceStore.getLastUpdated(type);
        assertPersistedDate(currentDate, lastUpdated);
    }

    private void assertPersistedDate(Date date, String lastUpdated) throws ParseException {
        SimpleDateFormat
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
        Date lastUpdatedDate = simpleDateFormat.parse(lastUpdated);

        assertTrue(date.compareTo(lastUpdatedDate) == 0);
    }
}
