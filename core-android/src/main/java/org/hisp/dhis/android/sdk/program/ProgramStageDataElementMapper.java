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
import org.hisp.dhis.android.sdk.flow.ProgramStageDataElement$Flow;
import org.hisp.dhis.java.sdk.models.program.ProgramStageDataElement;

public class ProgramStageDataElementMapper extends AbsMapper<ProgramStageDataElement, ProgramStageDataElement$Flow> {

    public ProgramStageDataElementMapper() {
        // empty constructor
    }

    @Override
    public ProgramStageDataElement$Flow mapToDatabaseEntity(ProgramStageDataElement programStageDataElement) {
        if (programStageDataElement == null) {
            return null;
        }

        ProgramStageDataElement$Flow programStageDataElementFlow = new ProgramStageDataElement$Flow();
        programStageDataElementFlow.setProgramStage(programStageDataElement.getProgramStage());
        programStageDataElementFlow.setDataElement(programStageDataElement.getDataElement());
        programStageDataElementFlow.setAllowFutureDate(programStageDataElement.isAllowFutureDate());
        programStageDataElementFlow.setSortOrder(programStageDataElement.getSortOrder());
        programStageDataElementFlow.setDisplayInReports(programStageDataElement.isDisplayInReports());
        programStageDataElementFlow.setAllowProvidedElsewhere(programStageDataElement.isAllowProvidedElsewhere());
        programStageDataElementFlow.setCompulsory(programStageDataElement.isCompulsory());
        programStageDataElementFlow.setProgramStageSection(programStageDataElement.getProgramStageSection());
        return programStageDataElementFlow;
    }

    @Override
    public ProgramStageDataElement mapToModel(ProgramStageDataElement$Flow programStageDataElementFlow) {
        if (programStageDataElementFlow == null) {
            return null;
        }

        ProgramStageDataElement programStageDataElement = new ProgramStageDataElement();
        programStageDataElement.setProgramStage(programStageDataElementFlow.getProgramStage());
        programStageDataElement.setDataElement(programStageDataElementFlow.getDataElement());
        programStageDataElement.setAllowFutureDate(programStageDataElementFlow.isAllowFutureDate());
        programStageDataElement.setSortOrder(programStageDataElementFlow.getSortOrder());
        programStageDataElement.setDisplayInReports(programStageDataElementFlow.isDisplayInReports());
        programStageDataElement.setAllowProvidedElsewhere(programStageDataElementFlow.isAllowProvidedElsewhere());
        programStageDataElement.setCompulsory(programStageDataElementFlow.isCompulsory());
        programStageDataElement.setProgramStageSection(programStageDataElementFlow.getProgramStageSection());
        return programStageDataElement;
    }

    @Override
    public Class<ProgramStageDataElement> getModelTypeClass() {
        return ProgramStageDataElement.class;
    }

    @Override
    public Class<ProgramStageDataElement$Flow> getDatabaseEntityTypeClass() {
        return ProgramStageDataElement$Flow.class;
    }
}
