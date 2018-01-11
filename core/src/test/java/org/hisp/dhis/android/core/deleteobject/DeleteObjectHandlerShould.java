package org.hisp.dhis.android.core.deleteobject;

import static org.mockito.Mockito.verify;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboStore;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.CategoryOptionComboStore;
import org.hisp.dhis.android.core.category.CategoryOptionStore;
import org.hisp.dhis.android.core.category.CategoryStore;
import org.hisp.dhis.android.core.constant.ConstantStore;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.deletedobject.DeletedObject;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectHandler;
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
import org.hisp.dhis.android.core.user.UserStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
public class DeleteObjectHandlerShould {

    @Mock
    private UserStore userStore;
    @Mock
    private CategoryStore categoryStore;
    @Mock
    private CategoryOptionStore categoryOptionStore;
    @Mock
    private CategoryComboStore categoryComboStore;
    @Mock
    private CategoryOptionComboStore categoryOptionComboStore;
    @Mock
    private ProgramStore programStore;
    @Mock
    private OrganisationUnitStore organisationUnitStore;
    @Mock
    private OptionSetStore optionSetStore;
    @Mock
    private TrackedEntityStore trackedEntityStore;
    @Mock
    private DataElementStore dataElementStore;
    @Mock
    private OptionStore optionStore;
    @Mock
    private ProgramIndicatorStore programIndicatorStore;
    @Mock
    private ProgramRuleStore programRuleStore;
    @Mock
    private ProgramRuleActionStore programRuleActionStore;
    @Mock
    private ProgramRuleVariableStore programRuleVariableStore;
    @Mock
    private ProgramStageStore programStageStore;
    @Mock
    private ProgramStageDataElementStore programStageDataElementStore;
    @Mock
    private ProgramStageSectionStore programStageSectionStore;
    @Mock
    private ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;
    @Mock
    private TrackedEntityAttributeStore trackedEntityAttributeStore;
    @Mock
    private RelationshipTypeStore relationshipTypeStore;

    private DeletedObjectHandler deletedObjectHandler;

    @Before
    public void setUp() throws Exception {


        MockitoAnnotations.initMocks(this);
        deletedObjectHandler = new DeletedObjectHandler(userStore,
                categoryStore, categoryComboStore, categoryOptionComboStore,
                programStore, organisationUnitStore, optionSetStore, trackedEntityStore,
                categoryOptionStore, dataElementStore, optionStore, programIndicatorStore, programRuleStore,
                programRuleActionStore, programRuleVariableStore, programStageStore, programStageDataElementStore,
                programStageSectionStore, programTrackedEntityAttributeStore, trackedEntityAttributeStore,
                relationshipTypeStore);
    }

    @Test
    public void handle_deleted_relationship_type() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(RelationshipType.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(relationshipTypeStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_tracked_entity_attribute() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(TrackedEntityAttribute.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(trackedEntityAttributeStore).delete(deletedObject.uid());
    }


    @Test
    public void handle_deleted_program_tracked_entity_attribute() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramTrackedEntityAttribute.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(programTrackedEntityAttributeStore).delete(deletedObject.uid());
    }


    @Test
    public void handle_deleted_program_stage_section() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramStageSection.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(programStageSectionStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program_stage_data_element() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramStageDataElement.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(programStageDataElementStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program_stage() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramStage.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(programStageStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program_rule_variable() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramRuleVariable.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(programRuleVariableStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program_rule_action() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramRuleAction.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(programRuleActionStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program_rule() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramRule.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(programRuleStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program_indicator() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(ProgramIndicator.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(programIndicatorStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_option() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(Option.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(optionStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_data_element() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(DataElement.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(dataElementStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_tracked_entity_store() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(TrackedEntity.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(trackedEntityStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_option_set() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(OptionSet.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(optionSetStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_organisation_units() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(OrganisationUnit.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(organisationUnitStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(Program.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(programStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_user() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(User.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(userStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_category_combo() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(CategoryCombo.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(categoryComboStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_category_option_combo() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(CategoryOptionCombo.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(categoryOptionComboStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_category_option() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(CategoryOption.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(categoryOptionStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_category() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(Category.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(categoryStore).delete(deletedObject.uid());
    }


    @Test(expected = NullPointerException.class)
    public void throw_null_exception_when_handle_deleted_unsupported_type() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(UserCredentials.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), type);

        //then
        verify(programStageDataElementStore).delete(deletedObject.uid());
    }


    private DeletedObject givenADeletedObjectByClass(String klass) {
        return DeletedObject.create("xxx", klass, null, "");
    }
}