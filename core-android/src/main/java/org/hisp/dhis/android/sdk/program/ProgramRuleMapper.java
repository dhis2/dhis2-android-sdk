/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.program;

import org.hisp.dhis.android.sdk.common.base.AbsMapper;
import org.hisp.dhis.android.sdk.common.base.IMapper;
import org.hisp.dhis.android.sdk.flow.ProgramRule$Flow;
import org.hisp.dhis.android.sdk.flow.ProgramRuleAction$Flow;
import org.hisp.dhis.java.sdk.models.program.ProgramRule;
import org.hisp.dhis.java.sdk.models.program.ProgramRuleAction;

public class ProgramRuleMapper extends AbsMapper<ProgramRule, ProgramRule$Flow> {

    private final IMapper<ProgramRuleAction, ProgramRuleAction$Flow> programRuleActionMapper;

    public ProgramRuleMapper(IMapper<ProgramRuleAction, ProgramRuleAction$Flow> programRuleActionMapper) {
        this.programRuleActionMapper = programRuleActionMapper;
    }

    @Override
    public ProgramRule$Flow mapToDatabaseEntity(ProgramRule programRule) {
        if (programRule == null) {
            return null;
        }

        ProgramRule$Flow programRuleFlow = new ProgramRule$Flow();
        programRuleFlow.setId(programRule.getId());
        programRuleFlow.setUId(programRule.getUId());
        programRuleFlow.setCreated(programRule.getCreated());
        programRuleFlow.setLastUpdated(programRule.getLastUpdated());
        programRuleFlow.setName(programRule.getName());
        programRuleFlow.setDisplayName(programRule.getDisplayName());
        programRuleFlow.setAccess(programRule.getAccess());
        programRuleFlow.setProgramStage(programRule.getProgramStage());
        programRuleFlow.setProgram(programRule.getProgram());
        programRuleFlow.setCondition(programRule.getCondition());
        programRuleFlow.setExternalAction(programRule.isExternalAction());
        programRuleFlow.setProgramRuleActions(programRuleActionMapper.mapToDatabaseEntities(programRule.getProgramRuleActions()));
        return programRuleFlow;
    }

    @Override
    public ProgramRule mapToModel(ProgramRule$Flow programRuleFlow) {
        if (programRuleFlow == null) {
            return null;
        }

        ProgramRule programRule = new ProgramRule();
        programRule.setId(programRuleFlow.getId());
        programRule.setUId(programRuleFlow.getUId());
        programRule.setCreated(programRuleFlow.getCreated());
        programRule.setLastUpdated(programRuleFlow.getLastUpdated());
        programRule.setName(programRuleFlow.getName());
        programRule.setDisplayName(programRuleFlow.getDisplayName());
        programRule.setAccess(programRuleFlow.getAccess());
        programRule.setProgramStage(programRuleFlow.getProgramStage());
        programRule.setProgram(programRuleFlow.getProgram());
        programRule.setCondition(programRuleFlow.getCondition());
        programRule.setExternalAction(programRuleFlow.isExternalAction());
        programRule.setProgramRuleActions(programRuleActionMapper.mapToModels(programRuleFlow.getProgramRuleActions()));
        return programRule;
    }

    @Override
    public Class<ProgramRule> getModelTypeClass() {
        return ProgramRule.class;
    }

    @Override
    public Class<ProgramRule$Flow> getDatabaseEntityTypeClass() {
        return ProgramRule$Flow.class;
    }
}
