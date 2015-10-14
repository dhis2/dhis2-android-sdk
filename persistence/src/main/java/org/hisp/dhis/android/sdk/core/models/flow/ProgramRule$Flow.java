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

package org.hisp.dhis.android.sdk.core.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.program.ProgramRule;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class ProgramRule$Flow extends BaseIdentifiableObject$Flow {

    @Column
    String programStage;

    @Column
    String program;

    @Column
    String condition;

    @Column
    boolean externalAction;

    List<ProgramRuleAction$Flow> programRuleActions;

    public String getProgramStage() {
        return programStage;
    }

    public void setProgramStage(String programStage) {
        this.programStage = programStage;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean isExternalAction() {
        return externalAction;
    }

    public void setExternalAction(boolean externalAction) {
        this.externalAction = externalAction;
    }

    public List<ProgramRuleAction$Flow> getProgramRuleActions() {
        return programRuleActions;
    }

    public void setProgramRuleActions(List<ProgramRuleAction$Flow> programRuleActions) {
        this.programRuleActions = programRuleActions;
    }

    public ProgramRule$Flow() {
        // empty constructor
    }

    public static ProgramRule toModel(ProgramRule$Flow programRuleFlow) {
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
        programRule.setProgramRuleActions(ProgramRuleAction$Flow.toModels(programRuleFlow.getProgramRuleActions()));
        return programRule;
    }

    public static ProgramRule$Flow fromModel(ProgramRule programRule) {
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
        programRuleFlow.setProgramRuleActions(ProgramRuleAction$Flow.fromModels(programRule.getProgramRuleActions()));
        return programRuleFlow;
    }

    public static List<ProgramRule> toModels(List<ProgramRule$Flow> programRuleFlows) {
        List<ProgramRule> programRules = new ArrayList<>();

        if (programRuleFlows != null && !programRuleFlows.isEmpty()) {
            for (ProgramRule$Flow programRuleFlow : programRuleFlows) {
                programRules.add(toModel(programRuleFlow));
            }
        }

        return programRules;
    }

    public static List<ProgramRule$Flow> fromModels(List<ProgramRule> programRules) {
        List<ProgramRule$Flow> programRuleFlows = new ArrayList<>();

        if (programRules != null && !programRules.isEmpty()) {
            for (ProgramRule programRule : programRules) {
                programRuleFlows.add(fromModel(programRule));
            }
        }

        return programRuleFlows;
    }
}
