package org.hisp.dhis.android.core.audit;

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
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageMetadataAuditHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityFactory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityMetadataAuditHandler;

public class MetadataAuditHandlerFactory {

    private final TrackedEntityFactory trackedEntityFactory;
    private final OptionSetFactory optionSetFactory;
    private final ProgramFactory programFactory;

    public MetadataAuditHandlerFactory(
            TrackedEntityFactory trackedEntityFactory, OptionSetFactory optionSetFactory,
            ProgramFactory programFactory) {
        this.trackedEntityFactory = trackedEntityFactory;
        this.optionSetFactory = optionSetFactory;
        this.programFactory = programFactory;
    }

    public MetadataAuditHandler getByClass(Class<?> klass) {
        if (klass == TrackedEntity.class) {
            return new TrackedEntityMetadataAuditHandler(trackedEntityFactory);
        } else if (klass == OptionSet.class) {
            return new OptionSetMetadataAuditHandler(optionSetFactory);
        } else if (klass == Option.class) {
            return new OptionMetadataAuditHandler(optionSetFactory);
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
        } else {
            throw new IllegalArgumentException("No exists a metadata audit handler for: " + klass);
        }
    }

}
