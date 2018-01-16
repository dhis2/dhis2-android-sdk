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
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.user.User;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Response;

public class DeletedObjectEndpointCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;
    private DeletedObjectHandlerFactory deletedObjectHandlerFactory;
    private DeletedObjectHandler deletedObjectHandler;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
        deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(d2.databaseAdapter());
        deletedObjectHandler = new DeletedObjectHandler(deletedObjectHandlerFactory);
    }

    @Test
    @LargeTest
    public void return_empty_deleted_object_programs_when_is_called_a_second_time() throws Exception {
        //given
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        Date date = new Date();
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());


        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, Program.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());
        assertTrue(hasDeletedObjects(response));

        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_PROGRAM);
        assertPersistedDate(date, lastUpdated);

        //when
        response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, new Date(), Program.class.getSimpleName()).call();

        //then
        assertTrue(response.isSuccessful());
        assertTrue(hasEmptyDeleteObjects(response));
    }

    @Test
    @LargeTest
    public void return_deleted_programs_when_call_deleted_object_endpoint_using_program() throws Exception {
        //given
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, new ResourceStoreImpl(d2.databaseAdapter()),
                deletedObjectHandler, new Date(), Program.class.getSimpleName()).call();

        //then
        assertTrue(response.isSuccessful());
        assertTrue(hasDeletedObjects(response));
    }

    @Test
    @LargeTest
    public void persist_program_last_updated_when_program_deleted_object_is_called() throws Exception {
        //given
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        Date date = new Date();
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());


        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, Program.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());
        assertTrue(hasDeletedObjects(response));

        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_PROGRAM);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_relationship_type_last_updated_when_relationship_type_deleted_object_is_called() throws Exception {
        //given
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        Date date = new Date();
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());


        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, RelationshipType.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_RELATIONSHIP_TYPE);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_tracked_entity_attribute_last_updated_when_tracked_entity_attribute_deleted_object_is_called() throws Exception {
        //given
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        Date date = new Date();
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());


        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, TrackedEntityAttribute.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_TRACKED_ENTITY_ATTRIBUTE);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_tracked_entity_last_updated_when_tracked_entity_deleted_object_is_called() throws Exception {
        //given
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        Date date = new Date();
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());


        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, TrackedEntity.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_TRACKED_ENTITY);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_program_stage_last_updated_when_program_stage_deleted_object_is_called() throws Exception {
        //given
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        Date date = new Date();
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());


        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, ProgramStage.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_PROGRAM_STAGE);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_program_stage_data_element_last_updated_when_program_stage_data_element_deleted_object_is_called() throws Exception {
        //given
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        Date date = new Date();
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());


        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, ProgramStageDataElement.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_PROGRAM_STAGE_DATA_ELEMENT);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_program_stage_section_last_updated_when_program_stage_section_deleted_object_is_called() throws Exception {

        //given
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        Date date = new Date();
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, ProgramStageSection.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_PROGRAM_STAGE_SECTION);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_program_rule_last_updated_when_program_rule_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, ProgramRule.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_PROGRAM_RULE);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_program_rule_action_last_updated_when_program_rule_action_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, ProgramRuleAction.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_PROGRAM_RULE_ACTION);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_program_rule_variable_last_updated_when_program_rule_variable_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, ProgramRuleVariable.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_PROGRAM_RULE_VARIABLE);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_program_indicator_last_updated_when_program_indicator_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, ProgramIndicator.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_PROGRAM_INDICATOR);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_option_last_updated_when_option_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, Option.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_OPTION);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_data_element_when_data_element_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, DataElement.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_DATA_ELEMENT);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_program_tracked_entity_attribute_when_program_tracked_entity_attribute_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, ProgramTrackedEntityAttribute.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_PROGRAM_TRACKED_ENTITY_ATTRIBUTE);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_option_set_when_option_set_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, OptionSet.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_OPTION_SET);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_organisation_units_when_organisation_units_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, OrganisationUnit.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_ORGANISATION_UNIT);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_user_when_user_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, User.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_USER);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_category_combo_when_category_combo_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, CategoryCombo.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_CATEGORY_COMBO);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_category_option_combo_when_category_option_combo_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, CategoryOptionCombo.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_CATEGORY_OPTION_COMBO);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_category_option_when_category_option_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, CategoryOption.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_CATEGORY_OPTION);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void persist_deleted_category_when_category_deleted_object_is_called() throws Exception {
        //given
        Date date = new Date();
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService, resourceStore,
                deletedObjectHandler, date, Category.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());

        //then
        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_CATEGORY);
        assertPersistedDate(date, lastUpdated);
    }

    private boolean hasDeletedObjects(Response<Payload<DeletedObject>> response) {
        return !response.body().items().isEmpty();
    }

    private boolean hasEmptyDeleteObjects(Response<Payload<DeletedObject>> response) {
        return response.body().items().isEmpty();
    }

    private void assertPersistedDate(Date date, String lastUpdated) throws ParseException {
        SimpleDateFormat
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
        Date lastUpdatedDate = simpleDateFormat.parse(lastUpdated);
        assertTrue(date.compareTo(lastUpdatedDate)==0);
    }
}
