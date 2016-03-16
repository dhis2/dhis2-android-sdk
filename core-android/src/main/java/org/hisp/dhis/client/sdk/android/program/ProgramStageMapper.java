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

import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageFlow;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;

public class ProgramStageMapper extends AbsMapper<ProgramStage, ProgramStageFlow> {

    @Override
    public ProgramStageFlow mapToDatabaseEntity(ProgramStage programStage) {
        if (programStage == null) {
            return null;
        }

        ProgramStageFlow programStageFlow = new ProgramStageFlow();
        programStageFlow.setId(programStage.getId());
        programStageFlow.setUId(programStage.getUId());
        programStageFlow.setCreated(programStage.getCreated());
        programStageFlow.setLastUpdated(programStage.getLastUpdated());
        programStageFlow.setName(programStage.getName());
        programStageFlow.setDisplayName(programStage.getDisplayName());
        programStageFlow.setAccess(programStage.getAccess());
        programStageFlow.setProgram(ProgramFlow.MAPPER
                .mapToDatabaseEntity(programStage.getProgram()));
        programStageFlow.setDataEntryType(programStage.getDataEntryType());
        programStageFlow.setBlockEntryForm(programStage.isBlockEntryForm());
        programStageFlow.setReportDateDescription(programStage.getReportDateDescription());
        programStageFlow.setDisplayGenerateEventBox(programStage.isDisplayGenerateEventBox());
        programStageFlow.setDescription(programStage.getDescription());
        programStageFlow.setExternalAccess(programStage.isExternalAccess());
        programStageFlow.setOpenAfterEnrollment(programStage.isOpenAfterEnrollment());
        programStageFlow.setCaptureCoordinates(programStage.isCaptureCoordinates());
        programStageFlow.setDefaultTemplateMessage(programStage.getDefaultTemplateMessage());
        programStageFlow.setRemindCompleted(programStage.isRemindCompleted());
        programStageFlow.setValidCompleteOnly(programStage.isValidCompleteOnly());
        programStageFlow.setSortOrder(programStage.getSortOrder());
        programStageFlow.setGeneratedByEnrollmentDate(programStage.isGeneratedByEnrollmentDate());
        programStageFlow.setPreGenerateUID(programStage.isPreGenerateUID());
        programStageFlow.setAutoGenerateEvent(programStage.isAutoGenerateEvent());
        programStageFlow.setAllowGenerateNextVisit(programStage.isAllowGenerateNextVisit());
        programStageFlow.setRepeatable(programStage.isRepeatable());
        programStageFlow.setMinDaysFromStart(programStage.getMinDaysFromStart());
        return programStageFlow;
    }

    @Override
    public ProgramStage mapToModel(ProgramStageFlow programStageFlow) {
        if (programStageFlow == null) {
            return null;
        }

        ProgramStage programStage = new ProgramStage();
        programStage.setId(programStageFlow.getId());
        programStage.setUId(programStageFlow.getUId());
        programStage.setCreated(programStageFlow.getCreated());
        programStage.setLastUpdated(programStageFlow.getLastUpdated());
        programStage.setName(programStageFlow.getName());
        programStage.setDisplayName(programStageFlow.getDisplayName());
        programStage.setAccess(programStageFlow.getAccess());
        programStage.setProgram(ProgramFlow.MAPPER
                .mapToModel(programStageFlow.getProgram()));
        programStage.setDataEntryType(programStageFlow.getDataEntryType());
        programStage.setBlockEntryForm(programStageFlow.isBlockEntryForm());
        programStage.setReportDateDescription(programStageFlow.getReportDateDescription());
        programStage.setDisplayGenerateEventBox(programStageFlow.isDisplayGenerateEventBox());
        programStage.setDescription(programStageFlow.getDescription());
        programStage.setExternalAccess(programStageFlow.isExternalAccess());
        programStage.setOpenAfterEnrollment(programStageFlow.isOpenAfterEnrollment());
        programStage.setCaptureCoordinates(programStageFlow.isCaptureCoordinates());
        programStage.setDefaultTemplateMessage(programStageFlow.getDefaultTemplateMessage());
        programStage.setRemindCompleted(programStageFlow.isRemindCompleted());
        programStage.setValidCompleteOnly(programStageFlow.isValidCompleteOnly());
        programStage.setSortOrder(programStageFlow.getSortOrder());
        programStage.setGeneratedByEnrollmentDate(programStageFlow.isGeneratedByEnrollmentDate());
        programStage.setPreGenerateUID(programStageFlow.isPreGenerateUID());
        programStage.setAutoGenerateEvent(programStageFlow.isAutoGenerateEvent());
        programStage.setAllowGenerateNextVisit(programStageFlow.isAllowGenerateNextVisit());
        programStage.setRepeatable(programStageFlow.isRepeatable());
        programStage.setMinDaysFromStart(programStageFlow.getMinDaysFromStart());
        return programStage;
    }

    @Override
    public Class<ProgramStage> getModelTypeClass() {
        return ProgramStage.class;
    }

    @Override
    public Class<ProgramStageFlow> getDatabaseEntityTypeClass() {
        return ProgramStageFlow.class;
    }
}
