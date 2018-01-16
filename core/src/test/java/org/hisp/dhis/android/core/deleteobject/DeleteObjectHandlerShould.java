package org.hisp.dhis.android.core.deleteobject;

import static junit.framework.Assert.assertTrue;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.hisp.dhis.android.core.user.UserStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    @Mock
    DatabaseAdapter mDatabaseAdapter = mock(DatabaseAdapter.class, Mockito.RETURNS_DEEP_STUBS);
    @Mock
    DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(mDatabaseAdapter);

    private DeletedObjectHandler deletedObjectHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        deletedObjectHandler = new DeletedObjectHandler(deletedObjectHandlerFactory);
    }

    @Test
    public void handle_deleted_relationship_type() {
        //given
        String klass = RelationshipType.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);


        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(relationshipTypeStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(relationshipTypeStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_tracked_entity_attribute() {
        //given
        String klass = TrackedEntityAttribute.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(trackedEntityAttributeStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(trackedEntityAttributeStore).delete(deletedObject.uid());
    }


    @Test
    public void handle_deleted_program_tracked_entity_attribute() {
        //given
        String klass = ProgramTrackedEntityAttribute.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(programTrackedEntityAttributeStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(programTrackedEntityAttributeStore).delete(deletedObject.uid());
    }


    @Test
    public void handle_deleted_program_stage_section() {
        //given
        String klass = ProgramStageSection.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(programStageSectionStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(programStageSectionStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program_stage_data_element() {
        //given
        String klass = ProgramStageDataElement.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(programStageDataElementStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(programStageDataElementStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program_stage() {
        //given
        String klass = ProgramStage.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(programStageStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(programStageStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program_rule_variable() {
        //given
        String klass = ProgramRuleVariable.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(programRuleVariableStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());

        //then
        verify(programRuleVariableStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program_rule_action() {
        //given
        String klass = ProgramRuleAction.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(programRuleActionStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(programRuleActionStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program_rule() {
        //given
        String klass = ProgramRule.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(programRuleStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(programRuleStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program_indicator() {
        //given
        String klass = ProgramIndicator.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(programIndicatorStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(programIndicatorStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_option() {
        //given
        String klass = Option.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(optionStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(optionStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_data_element() {
        //given
        String klass = DataElement.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(dataElementStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(dataElementStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_tracked_entity_store() {
        //given
        String klass = TrackedEntity.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(trackedEntityStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(trackedEntityStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_option_set() {
        //given
        String klass = OptionSet.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(optionSetStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(optionSetStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_organisation_units() {
        //given
        String klass = OrganisationUnit.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(organisationUnitStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(organisationUnitStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_program() {
        //given
        String klass = Program.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(programStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(programStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_user() {
        //given
        String klass = User.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(userStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(userStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_category_combo() {
        //given
        String klass = CategoryCombo.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(categoryComboStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(categoryComboStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_category_option_combo() {
        //given
        String klass = CategoryOptionCombo.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(categoryOptionComboStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(categoryOptionComboStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_category_option() {
        //given
        String klass = CategoryOption.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(categoryOptionStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(categoryOptionStore).delete(deletedObject.uid());
    }

    @Test
    public void handle_deleted_category() {
        //given
        String klass = Category.class.getSimpleName();
        DeletedObject deletedObject = givenADeletedObjectByClass(klass);

        //when
        when(deletedObjectHandlerFactory.getByKlass(klass)).thenReturn(categoryStore);
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());


        //then
        verify(categoryStore).delete(deletedObject.uid());
    }


    @Test(expected = NullPointerException.class)
    public void throw_null_exception_when_handle_deleted_unsupported_type() {
        //given
        DeletedObject deletedObject = givenADeletedObjectByClass(UserCredentials.class.getSimpleName());
        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObject.klass());

        //when
        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());

        //then
        verify(programStageDataElementStore).delete(deletedObject.uid());
    }


    private DeletedObject givenADeletedObjectByClass(String klass) {
        return DeletedObject.create("xxx", klass, null, "");
    }
}