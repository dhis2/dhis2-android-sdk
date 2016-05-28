package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.core.common.controllers.IdentifiableController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;

import java.util.List;

public interface ProgramRuleVariableController extends IdentifiableController<ProgramRuleVariable> {
    void pull(SyncStrategy strategy, List<Program> programList);
}
