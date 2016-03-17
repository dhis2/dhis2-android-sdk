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
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageFlow;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageDataElementFlow;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;

public class ProgramStageDataElementMapper extends AbsMapper<ProgramStageDataElement, ProgramStageDataElementFlow> {

    @Override
    public ProgramStageDataElementFlow mapToDatabaseEntity(ProgramStageDataElement programStageDataElement) {
        if (programStageDataElement == null) {
            return null;
        }

        ProgramStageDataElementFlow programStageDataElementFlow = new ProgramStageDataElementFlow();
        programStageDataElementFlow.setId(programStageDataElement.getId());
        programStageDataElementFlow.setUId(programStageDataElement.getUId());
        programStageDataElementFlow.setCreated(programStageDataElement.getCreated());
        programStageDataElementFlow.setLastUpdated(programStageDataElement.getLastUpdated());
        programStageDataElementFlow.setName(programStageDataElement.getName());
        programStageDataElementFlow.setDisplayName(programStageDataElement.getDisplayName());
        programStageDataElementFlow.setAccess(programStageDataElement.getAccess());
        programStageDataElementFlow.setProgramStageSection(MapperModuleProvider.getInstance().getProgramStageSectionMapper().mapToDatabaseEntity(programStageDataElement.getProgramStageSection()));
        programStageDataElementFlow.setProgramStage(ProgramStageFlow.MAPPER
                .mapToDatabaseEntity(programStageDataElement.getProgramStage()));
        programStageDataElementFlow.setDataElement(MapperModuleProvider.getInstance().getDataElementMapper().mapToDatabaseEntity(programStageDataElement.getDataElement()));
        programStageDataElementFlow.setAllowFutureDate(programStageDataElement.isAllowFutureDate());
        programStageDataElementFlow.setSortOrder(programStageDataElement.getSortOrder());
        programStageDataElementFlow.setDisplayInReports(programStageDataElement.isDisplayInReports());
        programStageDataElementFlow.setAllowProvidedElsewhere(programStageDataElement.isAllowProvidedElsewhere());
        programStageDataElementFlow.setCompulsory(programStageDataElement.isCompulsory());
        return programStageDataElementFlow;
    }

    @Override
    public ProgramStageDataElement mapToModel(ProgramStageDataElementFlow programStageDataElementFlow) {
        if (programStageDataElementFlow == null) {
            return null;
        }

        ProgramStageDataElement programStageDataElement = new ProgramStageDataElement();
        programStageDataElement.setId(programStageDataElementFlow.getId());
        programStageDataElement.setUId(programStageDataElementFlow.getUId());
        programStageDataElement.setCreated(programStageDataElementFlow.getCreated());
        programStageDataElement.setLastUpdated(programStageDataElementFlow.getLastUpdated());
        programStageDataElement.setName(programStageDataElementFlow.getName());
        programStageDataElement.setDisplayName(programStageDataElementFlow.getDisplayName());
        programStageDataElement.setAccess(programStageDataElementFlow.getAccess());
        programStageDataElement.setProgramStage(ProgramStageFlow.MAPPER
                .mapToModel(programStageDataElementFlow.getProgramStage()));
        programStageDataElement.setDataElement(MapperModuleProvider.getInstance().getDataElementMapper().mapToModel(programStageDataElementFlow.getDataElement()));
        programStageDataElement.setAllowFutureDate(programStageDataElementFlow.isAllowFutureDate());
        programStageDataElement.setSortOrder(programStageDataElementFlow.getSortOrder());
        programStageDataElement.setDisplayInReports(programStageDataElementFlow.isDisplayInReports());
        programStageDataElement.setAllowProvidedElsewhere(programStageDataElementFlow.isAllowProvidedElsewhere());
        programStageDataElement.setCompulsory(programStageDataElementFlow.isCompulsory());
        programStageDataElement.setProgramStageSection(MapperModuleProvider.getInstance().getProgramStageSectionMapper().mapToModel(programStageDataElementFlow.getProgramStageSection()));
        return programStageDataElement;
    }

    @Override
    public Class<ProgramStageDataElement> getModelTypeClass() {
        return ProgramStageDataElement.class;
    }

    @Override
    public Class<ProgramStageDataElementFlow> getDatabaseEntityTypeClass() {
        return ProgramStageDataElementFlow.class;
    }
}
