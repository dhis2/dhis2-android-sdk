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

import org.hisp.dhis.client.sdk.android.api.persistence.MapperModuleProvider;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramRuleVariableFlow;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;

public class ProgramRuleVariableMapper extends AbsMapper<ProgramRuleVariable, ProgramRuleVariableFlow> {

    public ProgramRuleVariableMapper() {
        // empty constructor
    }

    @Override
    public ProgramRuleVariableFlow mapToDatabaseEntity(ProgramRuleVariable programRuleVariable) {
        if (programRuleVariable == null) {
            return null;
        }

        ProgramRuleVariableFlow programRuleVariableFlow = new ProgramRuleVariableFlow();
        programRuleVariableFlow.setId(programRuleVariable.getId());
        programRuleVariableFlow.setUId(programRuleVariable.getUId());
        programRuleVariableFlow.setCreated(programRuleVariable.getCreated());
        programRuleVariableFlow.setLastUpdated(programRuleVariable.getLastUpdated());
        programRuleVariableFlow.setName(programRuleVariable.getName());
        programRuleVariableFlow.setDisplayName(programRuleVariable.getDisplayName());
        programRuleVariableFlow.setAccess(programRuleVariable.getAccess());
        programRuleVariableFlow.setDataElement(MapperModuleProvider.getInstance().getDataElementMapper().mapToDatabaseEntity(programRuleVariable.getDataElement()));
        programRuleVariableFlow.setTrackedEntityAttribute(MapperModuleProvider.getInstance().getTrackedEntityAttributeMapper().mapToDatabaseEntity(programRuleVariable.getTrackedEntityAttribute()));
        programRuleVariableFlow.setSourceType(programRuleVariable.getSourceType());
        programRuleVariableFlow.setProgram(MapperModuleProvider.getInstance().getProgramMapper().mapToDatabaseEntity(programRuleVariable.getProgram()));
        programRuleVariableFlow.setProgramStage(MapperModuleProvider.getInstance().getProgramStageMapper().mapToDatabaseEntity(programRuleVariable.getProgramStage()));
        return programRuleVariableFlow;
    }

    @Override
    public ProgramRuleVariable mapToModel(ProgramRuleVariableFlow programRuleVariableFlow) {
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
        programRuleVariable.setDataElement(MapperModuleProvider.getInstance().getDataElementMapper().mapToModel(programRuleVariableFlow.getDataElement()));
        programRuleVariable.setTrackedEntityAttribute(MapperModuleProvider.getInstance().getTrackedEntityAttributeMapper().mapToModel(programRuleVariableFlow.getTrackedEntityAttribute()));
        programRuleVariable.setSourceType(programRuleVariableFlow.getSourceType());
        programRuleVariable.setProgram(MapperModuleProvider.getInstance().getProgramMapper().mapToModel(programRuleVariableFlow.getProgram()));
        programRuleVariable.setProgramStage(MapperModuleProvider.getInstance().getProgramStageMapper().mapToModel(programRuleVariableFlow.getProgramStage()));
        return programRuleVariable;
    }

    @Override
    public Class<ProgramRuleVariable> getModelTypeClass() {
        return ProgramRuleVariable.class;
    }

    @Override
    public Class<ProgramRuleVariableFlow> getDatabaseEntityTypeClass() {
        return ProgramRuleVariableFlow.class;
    }
}
