package org.hisp.dhis.android.core.audit;

import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementFactory;
import org.hisp.dhis.android.core.dataelement.DataElementMetadataAuditHandler;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionMetadataAuditHandler;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.option.OptionSetMetadataAuditHandler;
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

    public MetadataAuditHandlerFactory(
            TrackedEntityFactory trackedEntityFactory, OptionSetFactory optionSetFactory,
            DataElementFactory dataElementFactory,
            TrackedEntityAttributeFactory trackedEntityAttributeFactory,
            ProgramFactory programFactory,
            RelationshipTypeFactory relationshipTypeFactory) {
        this.trackedEntityFactory = trackedEntityFactory;
        this.optionSetFactory = optionSetFactory;
        this.dataElementFactory = dataElementFactory;
        this.trackedEntityAttributeFactory = trackedEntityAttributeFactory;
        this.programFactory = programFactory;
        this.relationshipTypeFactory = relationshipTypeFactory;
    }

    public MetadataAuditHandler getByClass(Class<?> klass) {
        if (klass == TrackedEntity.class) {
            return new TrackedEntityMetadataAuditHandler(trackedEntityFactory);
        } else if (klass == OptionSet.class) {
            return new OptionSetMetadataAuditHandler(optionSetFactory);
        } else if (klass == Option.class) {
            return new OptionMetadataAuditHandler(optionSetFactory);
        } else if (klass == TrackedEntityAttribute.class) {
            return new TrackedEntityAttributeMetadataAuditHandler(
                    trackedEntityAttributeFactory);
        } else if (klass == DataElement.class) {
            return new DataElementMetadataAuditHandler(dataElementFactory);
        } else if (klass == Program.class) {
            return new ProgramMetadataAuditHandler(programFactory);
        } else if (klass == ProgramStage.class) {
            return new ProgramStageMetadataAuditHandler(programFactory);
        } else if (klass == ProgramIndicator.class) {
            return new ProgramIndicatorMetadataAuditHandler(programFactory);
        } else if (klass == ProgramRule.class) {
            return new ProgramRuleMetadataAuditHandler(programFactory);
        } else if (klass == ProgramRuleAction.class) {
            return new ProgramRuleActionMetadataAuditHandler(programFactory);
        } else if (klass == ProgramRuleVariable.class) {
            return new ProgramRuleVariableMetadataAuditHandler(programFactory);
        } else if (klass == RelationshipType.class) {
            return new RelationshipTypeMetadataAuditHandler(relationshipTypeFactory);
        } else {
            throw new IllegalArgumentException("No exists a metadata audit handler for: " + klass);
        }
    }

}
