package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.core.common.controllers.IdentifiableController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;

import java.util.List;

public interface ProgramRuleController extends IdentifiableController<ProgramRule> {
    void pull(SyncStrategy strategy, ProgramFields fields, List<Program> programList) throws ApiException;

    /**
     * Will pull down program rules with descendant properties for given programs.
     *
     * @param programs List of programs for which we need to pull program rules
     * @return List of operations which can be applied to database
     * @throws ApiException
     */
    List<DbOperation> pull(List<Program> programs) throws ApiException;
}
