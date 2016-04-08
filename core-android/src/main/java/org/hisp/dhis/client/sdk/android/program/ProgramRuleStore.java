/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.android.program;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.ModelLinkFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramRuleFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramRuleFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleStore;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ProgramRuleStore extends AbsIdentifiableObjectStore<ProgramRule,
        ProgramRuleFlow> implements IProgramRuleStore {
    private static final String PROGRAMRULE_TO_PROGRAMRULEACTIONS =
            "programRuleToProgramRuleActions";
    private final TransactionManager transactionManager;

    public ProgramRuleStore(TransactionManager transactionManager) {
        super(ProgramRuleFlow.MAPPER);
        this.transactionManager = transactionManager;
    }

    @Override
    public boolean insert(ProgramRule object) {
        boolean isSuccess = super.insert(object);

        if (isSuccess) {
            updateProgramStageRelationships(object);
        }

        return isSuccess;
    }

    @Override
    public boolean update(ProgramRule object) {
        boolean isSuccess = super.update(object);

        if (isSuccess) {
            updateProgramStageRelationships(object);
        }

        return isSuccess;
    }

    @Override
    public boolean save(ProgramRule object) {
        boolean isSuccess = super.save(object);

        if (isSuccess) {
            updateProgramStageRelationships(object);
        }
        return isSuccess;
    }

    @Override
    public boolean delete(ProgramRule object) {
        boolean isSuccess = super.delete(object);

        if (isSuccess) {
            ModelLinkFlow.deleteRelatedModels(object, PROGRAMRULE_TO_PROGRAMRULEACTIONS);
        }
        return isSuccess;
    }

    @Override
    public boolean deleteAll() {
        boolean isSuccess = super.deleteAll();

        if (isSuccess) {
            ModelLinkFlow.deleteModels(PROGRAMRULE_TO_PROGRAMRULEACTIONS);
        }
        return isSuccess;
    }

    @Override
    public ProgramRule queryById(long id) {
        return queryProgramRuleRelationships(super.queryById(id));
    }

    @Override
    public ProgramRule queryByUid(String uid) {
        return queryProgramRuleRelationships(super.queryByUid(uid));
    }

    @Override
    public List<ProgramRule> queryByUids(Set<String> uids) {
        return queryProgramRuleRelationships(super.queryByUids(uids));
    }

    @Override
    public List<ProgramRule> query(Program program) {
        List<ProgramRuleFlow> programRuleFlows = new Select()
                .from(ProgramRuleFlow.class)
                .where(ProgramRuleFlow_Table
                        .program.is(program.getUId()))
                .queryList();

        return getMapper().mapToModels(programRuleFlows);
    }

    @Override
    public List<ProgramRule> query(List<Program> programs) {
        List<ProgramRuleFlow> programRuleFlows = new Select()
                .from(ProgramRuleFlow.class)
                .where(ProgramRuleFlow_Table
                        .program.in(ModelUtils.toUidSet(programs)))
                .queryList();

        return getMapper().mapToModels(programRuleFlows);
    }

    @Override
    public List<ProgramRule> query(ProgramStage programStage) {
        List<ProgramRuleFlow> programRuleFlows = new Select()
                .from(ProgramRuleFlow.class)
                .where(ProgramRuleFlow_Table
                        .programstage.is(programStage.getUId()))
                .queryList();

        return getMapper().mapToModels(programRuleFlows);
    }

    private void updateProgramStageRelationships(ProgramRule programRule) {
        List<DbOperation> dbOperations = new ArrayList<>();
        dbOperations.addAll(ModelLinkFlow.updateLinksToModel(programRule,
                programRule.getProgramRuleActions(),
                PROGRAMRULE_TO_PROGRAMRULEACTIONS));
        transactionManager.transact(dbOperations);
    }

    private List<ProgramRule> queryProgramRuleRelationships(
            List<ProgramRule> programRules) {
        if (programRules != null) {
            Map<String, List<ProgramRuleAction>> rulesToActions = ModelLinkFlow
                    .queryLinksForModel(ProgramRuleAction.class,
                            PROGRAMRULE_TO_PROGRAMRULEACTIONS);
            for (ProgramRule programRule : programRules) {
                programRule.setProgramRuleActions(rulesToActions.get
                        (programRule.getUId()));
            }
        }

        return programRules;
    }

    private ProgramRule queryProgramRuleRelationships(ProgramRule programRule) {
        if (programRule != null) {
            List<ProgramRuleAction> programRuleActions = ModelLinkFlow
                    .queryLinksForModel(ProgramRuleAction.class,
                            PROGRAMRULE_TO_PROGRAMRULEACTIONS, programRule.getUId());
            programRule.setProgramRuleActions(programRuleActions);
        }

        return programRule;
    }
}
