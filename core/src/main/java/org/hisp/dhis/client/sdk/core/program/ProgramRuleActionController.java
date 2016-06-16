package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.core.common.controllers.IdentifiableController;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;

import java.util.List;

public interface ProgramRuleActionController extends IdentifiableController<ProgramRuleAction> {
    List<DbOperation> merge(List<ProgramRuleAction> programRuleActions) throws ApiException;
}
