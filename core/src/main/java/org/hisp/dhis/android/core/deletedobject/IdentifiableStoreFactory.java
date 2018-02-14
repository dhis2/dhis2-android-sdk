package org.hisp.dhis.android.core.deletedobject;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.CategoryOptionComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionStoreImpl;
import org.hisp.dhis.android.core.category.CategoryStoreImpl;
import org.hisp.dhis.android.core.common.IdentifiableStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementStoreImpl;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetStoreImpl;
import org.hisp.dhis.android.core.option.OptionStoreImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStoreImpl;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramIndicatorStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleActionStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramRuleVariableStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageSection;
import org.hisp.dhis.android.core.program.ProgramStageSectionStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStoreImpl;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStoreImpl;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStoreImpl;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserStoreImpl;


@SuppressWarnings({
        "PMD.ExcessiveImports",
        "PMD.CyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.CouplingBetweenObjects"
})
public class IdentifiableStoreFactory {
    private final DatabaseAdapter databaseAdapter;

    public IdentifiableStoreFactory(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    @SuppressWarnings({
            "PMD.NPathComplexity",
            "PMD.CyclomaticComplexity",
            "PMD.ModifiedCyclomaticComplexity",
            "PMD.StdCyclomaticComplexity"
    })
    public IdentifiableStore getByKlass(String klass) {
        if (klass.equals(User.class.getSimpleName())) {
            return new UserStoreImpl(databaseAdapter);
        }
        if (klass.equals(Category.class.getSimpleName())) {
            return new CategoryStoreImpl(databaseAdapter);
        }
        if (klass.equals(CategoryCombo.class.getSimpleName())) {
            return new CategoryComboStoreImpl(databaseAdapter);
        }
        if (klass.equals(CategoryOptionCombo.class.getSimpleName())) {
            return new CategoryOptionComboStoreImpl(databaseAdapter);
        }
        if (klass.equals(Program.class.getSimpleName())) {
            return new ProgramStoreImpl(databaseAdapter);
        }
        if (klass.equals(OrganisationUnit.class.getSimpleName())) {
            return new OrganisationUnitStoreImpl(databaseAdapter);
        }
        if (klass.equals(OptionSet.class.getSimpleName())) {
            return new OptionSetStoreImpl(databaseAdapter);
        }
        if (klass.equals(TrackedEntity.class.getSimpleName())) {
            return new TrackedEntityStoreImpl(databaseAdapter);
        }
        if (klass.equals(CategoryOption.class.getSimpleName())) {
            return new CategoryOptionStoreImpl(databaseAdapter);
        }
        if (klass.equals(DataElement.class.getSimpleName())) {
            return new DataElementStoreImpl(databaseAdapter);
        }
        if (klass.equals(Option.class.getSimpleName())) {
            return new OptionStoreImpl(databaseAdapter);
        }
        if (klass.equals(ProgramIndicator.class.getSimpleName())) {
            return new ProgramIndicatorStoreImpl(databaseAdapter);
        }
        if (klass.equals(ProgramRule.class.getSimpleName())) {
            return new ProgramRuleStoreImpl(databaseAdapter);
        }
        if (klass.equals(ProgramRuleAction.class.getSimpleName())) {
            return new ProgramRuleActionStoreImpl(databaseAdapter);
        }
        if (klass.equals(ProgramRuleVariable.class.getSimpleName())) {
            return new ProgramRuleVariableStoreImpl(databaseAdapter);
        }
        if (klass.equals(ProgramStage.class.getSimpleName())) {
            return new ProgramStageStoreImpl(databaseAdapter);
        }
        if (klass.equals(ProgramStageDataElement.class.getSimpleName())) {
            return new ProgramStageDataElementStoreImpl(databaseAdapter);
        }
        if (klass.equals(ProgramStageSection.class.getSimpleName())) {
            return new ProgramStageSectionStoreImpl(databaseAdapter);
        }
        if (klass.equals(ProgramTrackedEntityAttribute.class.getSimpleName())) {
            return new ProgramTrackedEntityAttributeStoreImpl(databaseAdapter);
        }
        if (klass.equals(TrackedEntityAttribute.class.getSimpleName())) {
            return new TrackedEntityAttributeStoreImpl(databaseAdapter);
        }
        if (klass.equals(RelationshipType.class.getSimpleName())) {
            return new RelationshipTypeStoreImpl(databaseAdapter);
        }
        if (klass.equals(DataElement.class.getSimpleName())) {
            return new DataElementStoreImpl(databaseAdapter);
        }
        throw new IllegalArgumentException("klass " + klass + "not supported ");
    }
}
