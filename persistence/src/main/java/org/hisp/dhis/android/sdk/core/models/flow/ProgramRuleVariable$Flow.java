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
import org.hisp.dhis.android.sdk.models.program.ProgramRuleVariable;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class ProgramRuleVariable$Flow extends BaseIdentifiableObject$Flow {

    @Column
    String dataElement;

    @Column
    String sourceType;

    @Column
    boolean externalAccess;

    @Column
    String program;

    public String getDataElement() {
        return dataElement;
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public boolean isExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public ProgramRuleVariable$Flow() {
        // empty constructor
    }

    public static ProgramRuleVariable toModel(ProgramRuleVariable$Flow programRuleVariableFlow) {
        if (programRuleVariableFlow == null) {
            return null;
        }

        ProgramRuleVariable programRuleVariable = new ProgramRuleVariable();
        programRuleVariable.setId(programRuleVariableFlow.getId());
        programRuleVariable.setUId(programRuleVariableFlow.getUId());
        programRuleVariable.setCreated(programRuleVariableFlow.getCreated());
        programRuleVariable.setLastUpdated(programRuleVariableFlow.getLastUpdated());
        programRuleVariable.setName(programRuleVariableFlow.getName());
        programRuleVariable.setDisplayName(programRuleVariableFlow.getDisplayName());
        programRuleVariable.setAccess(programRuleVariableFlow.getAccess());
        programRuleVariable.setDataElement(programRuleVariableFlow.getDataElement());
        programRuleVariable.setSourceType(programRuleVariableFlow.getSourceType());
        programRuleVariable.setExternalAccess(programRuleVariableFlow.isExternalAccess());
        programRuleVariable.setProgram(programRuleVariableFlow.getProgram());
        return programRuleVariable;
    }

    public static ProgramRuleVariable$Flow fromModel(ProgramRuleVariable programRuleVariable) {
        if (programRuleVariable == null) {
            return null;
        }

        ProgramRuleVariable$Flow programRuleVariableFlow = new ProgramRuleVariable$Flow();
        programRuleVariableFlow.setId(programRuleVariable.getId());
        programRuleVariableFlow.setUId(programRuleVariable.getUId());
        programRuleVariableFlow.setCreated(programRuleVariable.getCreated());
        programRuleVariableFlow.setLastUpdated(programRuleVariable.getLastUpdated());
        programRuleVariableFlow.setName(programRuleVariable.getName());
        programRuleVariableFlow.setDisplayName(programRuleVariable.getDisplayName());
        programRuleVariableFlow.setAccess(programRuleVariable.getAccess());
        programRuleVariableFlow.setDataElement(programRuleVariable.getDataElement());
        programRuleVariableFlow.setSourceType(programRuleVariable.getSourceType());
        programRuleVariableFlow.setExternalAccess(programRuleVariable.isExternalAccess());
        programRuleVariableFlow.setProgram(programRuleVariable.getProgram());
        return programRuleVariableFlow;
    }

    public static List<ProgramRuleVariable> toModels(List<ProgramRuleVariable$Flow> programRuleVariableFlows) {
        List<ProgramRuleVariable> programRuleVariables = new ArrayList<>();

        if (programRuleVariableFlows != null && !programRuleVariableFlows.isEmpty()) {
            for (ProgramRuleVariable$Flow programRuleVariableFlow : programRuleVariableFlows) {
                programRuleVariables.add(toModel(programRuleVariableFlow));
            }
        }

        return programRuleVariables;
    }

    public static List<ProgramRuleVariable$Flow> fromModels(List<ProgramRuleVariable> programRuleVariables) {
        List<ProgramRuleVariable$Flow> programRuleVariableFlows = new ArrayList<>();

        if (programRuleVariables != null && !programRuleVariables.isEmpty()) {
            for (ProgramRuleVariable programRuleVariable : programRuleVariables) {
                programRuleVariableFlows.add(fromModel(programRuleVariable));
            }
        }

        return programRuleVariableFlows;
    }
}
