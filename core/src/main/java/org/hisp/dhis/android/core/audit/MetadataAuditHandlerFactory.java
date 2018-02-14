package org.hisp.dhis.android.core.audit;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementFactory;
import org.hisp.dhis.android.core.dataelement.DataElementMetadataAuditHandler;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboFactory;
import org.hisp.dhis.android.core.category.CategoryComboMetadataAuditHandler;
import org.hisp.dhis.android.core.category.CategoryFactory;
import org.hisp.dhis.android.core.category.CategoryMetadataAuditHandler;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.CategoryOptionComboMetadataAuditHandler;
import org.hisp.dhis.android.core.category.CategoryOptionMetadataAuditHandler;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionMetadataAuditHandler;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.option.OptionSetMetadataAuditHandler;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitFactory;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMetadataAuditHandler;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramFactory;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramIndicatorMetadataAuditHandler;
import org.hisp.dhis.android.core.program.ProgramMetadataAuditHandler;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleActionMetadataAuditHandler;
import org.hisp.dhis.android.core.program.ProgramRuleMetadataAuditHandler;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramRuleVariableMetadataAuditHandler;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageMetadataAuditHandler;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeFactory;
import org.hisp.dhis.android.core.relationship.RelationshipTypeMetadataAuditHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeFactory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeMetadataAuditHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityFactory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityMetadataAuditHandler;

@SuppressWarnings({
        "PMD.CyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.ExcessiveImports"
})
public class MetadataAuditHandlerFactory {

    private final TrackedEntityFactory trackedEntityFactory;
    private final OptionSetFactory optionSetFactory;
    private final TrackedEntityAttributeFactory trackedEntityAttributeFactory;
    private final DataElementFactory dataElementFactory;
    private final ProgramFactory programFactory;
    private final RelationshipTypeFactory relationshipTypeFactory;
    private final OrganisationUnitFactory organisationUnitFactory;
    private final CategoryFactory categoryFactory;
    private final CategoryComboFactory categoryComboFactory;
    private final boolean isTranslationOn;
    private final String translationLocale;

    public MetadataAuditHandlerFactory(
            TrackedEntityFactory trackedEntityFactory, OptionSetFactory optionSetFactory,
            DataElementFactory dataElementFactory,
            TrackedEntityAttributeFactory trackedEntityAttributeFactory,
            ProgramFactory programFactory,
            RelationshipTypeFactory relationshipTypeFactory,
            OrganisationUnitFactory organisationUnitFactory,
            CategoryFactory categoryFactory, CategoryComboFactory categoryComboFactory
            , boolean isTranslationOn, @NonNull String translationLocale) {
        this.trackedEntityFactory = trackedEntityFactory;
        this.optionSetFactory = optionSetFactory;
        this.dataElementFactory = dataElementFactory;
        this.trackedEntityAttributeFactory = trackedEntityAttributeFactory;
        this.programFactory = programFactory;
        this.relationshipTypeFactory = relationshipTypeFactory;
        this.organisationUnitFactory = organisationUnitFactory;
        this.categoryFactory = categoryFactory;
        this.categoryComboFactory = categoryComboFactory;
        this.isTranslationOn = isTranslationOn;
        this.translationLocale = translationLocale;
    }

    public MetadataAuditHandler getByClass(Class<?> klass) {
        if (klass == TrackedEntity.class) {
            return new TrackedEntityMetadataAuditHandler(trackedEntityFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == OptionSet.class) {
            return new OptionSetMetadataAuditHandler(optionSetFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == Option.class) {
            return new OptionMetadataAuditHandler(optionSetFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == TrackedEntityAttribute.class) {
            return new TrackedEntityAttributeMetadataAuditHandler(
                    trackedEntityAttributeFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == DataElement.class) {
            return new DataElementMetadataAuditHandler(dataElementFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == Program.class) {
            return new ProgramMetadataAuditHandler(programFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == ProgramStage.class) {
            return new ProgramStageMetadataAuditHandler(programFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == ProgramIndicator.class) {
            return new ProgramIndicatorMetadataAuditHandler(programFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == ProgramRule.class) {
            return new ProgramRuleMetadataAuditHandler(programFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == ProgramRuleAction.class) {
            return new ProgramRuleActionMetadataAuditHandler(programFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == ProgramRuleVariable.class) {
            return new ProgramRuleVariableMetadataAuditHandler(programFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == RelationshipType.class) {
            return new RelationshipTypeMetadataAuditHandler(relationshipTypeFactory,
                    isTranslationOn, translationLocale);
        } else if (klass == OrganisationUnit.class) {
            return new OrganisationUnitMetadataAuditHandler(organisationUnitFactory,
                    isTranslationOn, translationLocale);
        } else if (klass == Category.class) {
            return new CategoryMetadataAuditHandler(categoryFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == CategoryOption.class) {
            return new CategoryOptionMetadataAuditHandler(categoryFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == CategoryCombo.class) {
            return new CategoryComboMetadataAuditHandler(categoryComboFactory, isTranslationOn,
                    translationLocale);
        } else if (klass == CategoryOptionCombo.class) {
            return new CategoryOptionComboMetadataAuditHandler(categoryComboFactory,
                    isTranslationOn, translationLocale);
        } else {
            throw new IllegalArgumentException("No exists a metadata audit handler for: " + klass);
        }
    }

}
