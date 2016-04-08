package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.core.common.controllers.IdentifiableController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;

import java.util.List;

public interface ProgramRuleController extends IdentifiableController<ProgramRule> {
    void pullUpdates(SyncStrategy strategy, List<Program> programList);
}
