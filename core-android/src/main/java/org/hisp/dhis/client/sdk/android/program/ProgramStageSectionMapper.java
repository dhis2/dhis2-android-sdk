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

import org.hisp.dhis.client.sdk.android.common.base.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.ProgramIndicator$Flow;
import org.hisp.dhis.client.sdk.android.flow.ProgramStageDataElement$Flow;
import org.hisp.dhis.client.sdk.android.flow.ProgramStageSection$Flow;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;

public class ProgramStageSectionMapper extends AbsMapper<ProgramStageSection, ProgramStageSection$Flow> {

    private final IMapper<ProgramStageDataElement, ProgramStageDataElement$Flow> programStageDataElementMapper;
    private final IMapper<ProgramIndicator, ProgramIndicator$Flow> programIndicatorMapper;

    public ProgramStageSectionMapper(IMapper<ProgramStageDataElement, ProgramStageDataElement$Flow> programStageDataElementMapper, IMapper<ProgramIndicator, ProgramIndicator$Flow> programIndicatorMapper) {
        this.programStageDataElementMapper = programStageDataElementMapper;
        // empty constructor
        this.programIndicatorMapper = programIndicatorMapper;
    }

    @Override
    public ProgramStageSection$Flow mapToDatabaseEntity(ProgramStageSection programStageSection) {
        if (programStageSection == null) {
            return null;
        }

        ProgramStageSection$Flow programStageSectionFlow = new ProgramStageSection$Flow();
        programStageSectionFlow.setId(programStageSection.getId());
        programStageSectionFlow.setUId(programStageSection.getUId());
        programStageSectionFlow.setCreated(programStageSection.getCreated());
        programStageSectionFlow.setLastUpdated(programStageSection.getLastUpdated());
        programStageSectionFlow.setName(programStageSection.getName());
        programStageSectionFlow.setDisplayName(programStageSection.getDisplayName());
        programStageSectionFlow.setAccess(programStageSection.getAccess());
        programStageSectionFlow.setSortOrder(programStageSection.getSortOrder());
        programStageSectionFlow.setExternalAccess(programStageSection.isExternalAccess());
        programStageSectionFlow.setProgramStage(programStageSection.getProgramStage());
        programStageSectionFlow.setProgramStageDataElements(programStageDataElementMapper.mapToDatabaseEntities(programStageSection.getProgramStageDataElements()));
        programStageSectionFlow.setProgramIndicators(programIndicatorMapper.mapToDatabaseEntities(programStageSection.getProgramIndicators()));
        return programStageSectionFlow;
    }

    @Override
    public ProgramStageSection mapToModel(ProgramStageSection$Flow programStageSectionFlow) {
        if (programStageSectionFlow == null) {
            return null;
        }

        ProgramStageSection programStageSection = new ProgramStageSection();
        programStageSection.setId(programStageSectionFlow.getId());
        programStageSection.setUId(programStageSectionFlow.getUId());
        programStageSection.setCreated(programStageSectionFlow.getCreated());
        programStageSection.setLastUpdated(programStageSectionFlow.getLastUpdated());
        programStageSection.setName(programStageSectionFlow.getName());
        programStageSection.setDisplayName(programStageSectionFlow.getDisplayName());
        programStageSection.setAccess(programStageSectionFlow.getAccess());
        programStageSection.setSortOrder(programStageSectionFlow.getSortOrder());
        programStageSection.setExternalAccess(programStageSectionFlow.isExternalAccess());
        programStageSection.setProgramStage(programStageSectionFlow.getProgramStage());
        programStageSection.setProgramStageDataElements(programStageDataElementMapper.mapToModels(programStageSectionFlow.getProgramStageDataElements()));
        programStageSection.setProgramIndicators(programIndicatorMapper.mapToModels(programStageSectionFlow.getProgramIndicators()));
        return programStageSection;
    }

    @Override
    public Class<ProgramStageSection> getModelTypeClass() {
        return ProgramStageSection.class;
    }

    @Override
    public Class<ProgramStageSection$Flow> getDatabaseEntityTypeClass() {
        return ProgramStageSection$Flow.class;
    }
}
