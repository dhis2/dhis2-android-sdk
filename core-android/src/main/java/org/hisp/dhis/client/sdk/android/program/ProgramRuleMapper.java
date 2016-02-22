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

import org.hisp.dhis.client.sdk.android.api.utils.MapperModuleProvider;
import org.hisp.dhis.client.sdk.android.common.base.AbsMapper;
import org.hisp.dhis.client.sdk.android.flow.ProgramRule$Flow;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;

public class ProgramRuleMapper extends AbsMapper<ProgramRule, ProgramRule$Flow> {

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
        programRuleFlow.setProgramStage(MapperModuleProvider.getInstance().getProgramStageMapper().mapToDatabaseEntity(programRule.getProgramStage()));
        programRuleFlow.setProgram(MapperModuleProvider.getInstance().getProgramMapper().mapToDatabaseEntity(programRule.getProgram()));
        programRuleFlow.setCondition(programRule.getCondition());
        programRuleFlow.setExternalAction(programRule.isExternalAction());
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
        programRule.setProgramStage(MapperModuleProvider.getInstance().getProgramStageMapper().mapToModel(programRuleFlow.getProgramStage()));
        programRule.setProgram(MapperModuleProvider.getInstance().getProgramMapper().mapToModel(programRuleFlow.getProgram()));
        programRule.setCondition(programRuleFlow.getCondition());
        programRule.setExternalAction(programRuleFlow.isExternalAction());
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
