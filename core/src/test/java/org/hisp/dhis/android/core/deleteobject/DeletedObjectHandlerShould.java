package org.hisp.dhis.android.core.deleteobject;

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
import org.hisp.dhis.android.core.common.IdentifiableStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.deletedobject.DeletedObject;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectHandler;
import org.hisp.dhis.android.core.deletedobject.IdentifiableStoreFactory;
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
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStore;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class DeletedObjectHandlerShould {

    @Parameterized.Parameters(name = "{index} invoke store delete : {0},{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {RelationshipType.class, RelationshipTypeStore.class},
                {TrackedEntityAttribute.class, TrackedEntityAttributeStore.class},
                {ProgramTrackedEntityAttribute.class, ProgramTrackedEntityAttributeStore.class},
                {ProgramStageSection.class, ProgramStageSectionStore.class},
                {ProgramStageDataElement.class, ProgramStageDataElementStore.class},
                {ProgramStage.class, ProgramStageStore.class},
                {ProgramRuleAction.class, ProgramRuleActionStore.class},
                {ProgramRuleVariable.class, ProgramRuleVariableStore.class},
                {ProgramRule.class, ProgramRuleStore.class},
                {ProgramIndicator.class, ProgramIndicatorStore.class},
                {Option.class, OptionStore.class},
                {DataElement.class, DataElementStore.class},
                {TrackedEntity.class, TrackedEntityStore.class},
                {OptionSet.class, OptionSetStore.class},
                {OrganisationUnit.class, OrganisationUnitStore.class},
                {User.class, UserStore.class},
                {Program.class, ProgramStore.class},
                {Category.class, CategoryStore.class},
                {CategoryOption.class, CategoryOptionStore.class},
                {CategoryCombo.class, CategoryComboStore.class},
                {CategoryOptionCombo.class, CategoryOptionComboStore.class},
        });
    }

    @Mock
    DatabaseAdapter mDatabaseAdapter = mock(DatabaseAdapter.class, Mockito.RETURNS_DEEP_STUBS);

    @Mock
    IdentifiableStoreFactory IdentifiableStoreFactory = new IdentifiableStoreFactory(
            mDatabaseAdapter);

    private DeletedObjectHandler deletedObjectHandler;

    private Class<?> deletedObjectClass;
    private Class<?> identifiableStoreClass;


    public DeletedObjectHandlerShould(Class<?> deletedObjectClass,
            Class<?> identifiableStoreClass) {
        this.deletedObjectClass = deletedObjectClass;
        this.identifiableStoreClass = identifiableStoreClass;
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        deletedObjectHandler = new DeletedObjectHandler(IdentifiableStoreFactory);
    }

    @Test
    public void invoke_delete_method_in_identifiable_store() {
        IdentifiableStore identifiableStore = (IdentifiableStore) mock(identifiableStoreClass);

        DeletedObject deletedObject = givenADeletedObjectByClass(
                deletedObjectClass.getSimpleName());

        when(IdentifiableStoreFactory.getByKlass(
                deletedObjectClass.getSimpleName())).thenReturn(identifiableStore);


        deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());

        verify(identifiableStore).delete(deletedObject.uid());
    }


    private DeletedObject givenADeletedObjectByClass(String klass) {
        return DeletedObject.create("xxx", klass, null, "");
    }
}
