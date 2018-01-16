package org.hisp.dhis.android.core.deleteobject;

import static junit.framework.Assert.assertTrue;

import static org.mockito.Mockito.verify;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboStore;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.CategoryOptionComboStore;
import org.hisp.dhis.android.core.category.CategoryOptionStore;
import org.hisp.dhis.android.core.category.CategoryStore;
import org.hisp.dhis.android.core.common.DeletableObjectStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.deletedobject.DeletedObject;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectHandler;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectHandlerFactory;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetStore;
import org.hisp.dhis.android.core.option.OptionStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramIndicatorStore;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleActionStore;
import org.hisp.dhis.android.core.program.ProgramRuleStore;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramRuleVariableStore;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStore;
import org.hisp.dhis.android.core.program.ProgramStageSection;
import org.hisp.dhis.android.core.program.ProgramStageSectionStore;
import org.hisp.dhis.android.core.program.ProgramStageStore;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStore;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStore;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.hisp.dhis.android.core.user.UserStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DeleteObjectHandlerFactoryShould {

    @Mock
    private DatabaseAdapter mDatabaseAdapter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handle_deleted_relationship_type() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(RelationshipType.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof RelationshipTypeStore);
    }

    @Test
    public void handle_deleted_tracked_entity_attribute() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(TrackedEntityAttribute.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof TrackedEntityAttributeStore);
    }


    @Test
    public void handle_deleted_program_tracked_entity_attribute() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramTrackedEntityAttribute.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof ProgramTrackedEntityAttributeStore);
    }


    @Test
    public void handle_deleted_program_stage_section() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramStageSection.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof ProgramStageSectionStore);
    }

    @Test
    public void handle_deleted_program_stage_data_element() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramStageDataElement.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof ProgramStageDataElementStore);
    }

    @Test
    public void handle_deleted_program_stage() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramStage.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof ProgramStageStore);
    }

    @Test
    public void handle_deleted_program_rule_variable() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramRuleVariable.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof ProgramRuleVariableStore);
    }

    @Test
    public void handle_deleted_program_rule_action() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramRuleAction.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof ProgramRuleActionStore);
    }

    @Test
    public void handle_deleted_program_rule() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramRule.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof ProgramRuleStore);
    }

    @Test
    public void handle_deleted_program_indicator() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramIndicator.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof ProgramIndicatorStore);
    }

    @Test
    public void handle_deleted_option() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(Option.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof OptionStore);
    }

    @Test
    public void handle_deleted_data_element() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(DataElement.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof DataElementStore);
    }

    @Test
    public void handle_deleted_tracked_entity_store() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(TrackedEntity.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof TrackedEntityStore);
    }

    @Test
    public void handle_deleted_option_set() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(OptionSet.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof OptionSetStore);
    }

    @Test
    public void handle_deleted_organisation_units() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(OrganisationUnit.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof OrganisationUnitStore);
    }

    @Test
    public void handle_deleted_program() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(Program.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof ProgramStore);
    }

    @Test
    public void handle_deleted_user() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(User.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof UserStore);
    }

    @Test
    public void handle_deleted_category_combo() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(CategoryCombo.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof CategoryComboStore);
    }

    @Test
    public void handle_deleted_category_option_combo() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(CategoryOptionCombo.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof CategoryOptionComboStore);
    }

    @Test
    public void handle_deleted_category_option() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(CategoryOption.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof CategoryOptionStore);
    }

    @Test
    public void handle_deleted_category() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(Category.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof CategoryStore);
    }


    @Test(expected = IllegalArgumentException.class)
    public void throw_null_exception_when_handle_deleted_unsupported_type() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(UserCredentials.class.getSimpleName());

        //when
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);
        DeletableObjectStore deletableObjectStore = deletedObjectHandlerFactory.getByKlass(deletedObject.klass());

        //then
        assertTrue(deletableObjectStore instanceof UserCredentialsStore);
    }


    private DeletedObject givenADeletedObjectByClass(String klass) {
        return DeletedObject.create("xxx", klass, null, "");
    }
}