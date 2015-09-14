/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.programruleaction.ProgramRuleAction;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class ProgramRuleAction$Flow extends BaseIdentifiableObject$Flow {
    @Column
    String programRule;

    @Column
    String dataElement;

    @Column
    String programStageSection;

    @Column
    String programRuleActionType;

    @Column
    boolean externalAccess;

    public String getProgramRule() {
        return programRule;
    }

    public void setProgramRule(String programRule) {
        this.programRule = programRule;
    }

    public String getDataElement() {
        return dataElement;
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }

    public String getProgramStageSection() {
        return programStageSection;
    }

    public void setProgramStageSection(String programStageSection) {
        this.programStageSection = programStageSection;
    }

    public String getProgramRuleActionType() {
        return programRuleActionType;
    }

    public void setProgramRuleActionType(String programRuleActionType) {
        this.programRuleActionType = programRuleActionType;
    }

    public boolean isExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public ProgramRuleAction$Flow() {
        // empty constructor
    }

    public static ProgramRuleAction toModel(ProgramRuleAction$Flow programRuleActionFlow) {
        if (programRuleActionFlow == null) {
            return null;
        }

        ProgramRuleAction programRuleAction = new ProgramRuleAction();
        programRuleAction.setId(programRuleActionFlow.getId());
        programRuleAction.setUId(programRuleActionFlow.getUId());
        programRuleAction.setCreated(programRuleActionFlow.getCreated());
        programRuleAction.setLastUpdated(programRuleActionFlow.getLastUpdated());
        programRuleAction.setName(programRuleActionFlow.getName());
        programRuleAction.setDisplayName(programRuleActionFlow.getDisplayName());
        programRuleAction.setAccess(programRuleActionFlow.getAccess());
        programRuleAction.setProgramRule(programRuleActionFlow.getProgramRule());
        programRuleAction.setDataElement(programRuleActionFlow.getDataElement());
        programRuleAction.setProgramStageSection(programRuleActionFlow.getProgramStageSection());
        programRuleAction.setProgramRuleActionType(programRuleActionFlow.getProgramRuleActionType());
        programRuleAction.setExternalAccess(programRuleActionFlow.isExternalAccess());
        return programRuleAction;
    }

    public static ProgramRuleAction$Flow fromModel(ProgramRuleAction programRuleAction) {
        if (programRuleAction == null) {
            return null;
        }

        ProgramRuleAction$Flow programRuleActionFlow = new ProgramRuleAction$Flow();
        programRuleActionFlow.setId(programRuleAction.getId());
        programRuleActionFlow.setUId(programRuleAction.getUId());
        programRuleActionFlow.setCreated(programRuleAction.getCreated());
        programRuleActionFlow.setLastUpdated(programRuleAction.getLastUpdated());
        programRuleActionFlow.setName(programRuleAction.getName());
        programRuleActionFlow.setDisplayName(programRuleAction.getDisplayName());
        programRuleActionFlow.setAccess(programRuleAction.getAccess());
        programRuleActionFlow.setProgramRule(programRuleAction.getProgramRule());
        programRuleActionFlow.setDataElement(programRuleAction.getDataElement());
        programRuleActionFlow.setProgramStageSection(programRuleAction.getProgramStageSection());
        programRuleActionFlow.setProgramRuleActionType(programRuleAction.getProgramRuleActionType());
        programRuleActionFlow.setExternalAccess(programRuleAction.isExternalAccess());
        return programRuleActionFlow;
    }

    public static List<ProgramRuleAction> toModels(List<ProgramRuleAction$Flow> programRuleActionFlows) {
        List<ProgramRuleAction> programRuleActions = new ArrayList<>();

        if (programRuleActionFlows != null && !programRuleActionFlows.isEmpty()) {
            for (ProgramRuleAction$Flow programRuleActionFlow : programRuleActionFlows) {
                programRuleActions.add(toModel(programRuleActionFlow));
            }
        }

        return programRuleActions;
    }

    public static List<ProgramRuleAction$Flow> fromModels(List<ProgramRuleAction> programRuleActions) {
        List<ProgramRuleAction$Flow> programRuleActionFlows = new ArrayList<>();

        if (programRuleActions != null && !programRuleActions.isEmpty()) {
            for (ProgramRuleAction programRuleAction : programRuleActions) {
                programRuleActionFlows.add(fromModel(programRuleAction));
            }
        }

        return programRuleActionFlows;
    }
}
