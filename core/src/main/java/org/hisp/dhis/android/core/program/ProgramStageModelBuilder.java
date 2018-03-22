/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.common.ModelBuilder;

public class ProgramStageModelBuilder extends ModelBuilder<ProgramStage, ProgramStageModel> {

    private final ProgramStageModel.Builder builder;

    ProgramStageModelBuilder(Program program) {
        this.builder = ProgramStageModel.builder()
            .program(program.uid());
    }
    
    @Override
    public ProgramStageModel buildModel(ProgramStage programStage) {
        return builder
                .uid(programStage.uid())
                .code(programStage.code())
                .name(programStage.name())
                .displayName(programStage.displayName())
                .created(programStage.created())
                .lastUpdated(programStage.lastUpdated())
                
                .executionDateLabel(programStage.executionDateLabel())
                .allowGenerateNextVisit(programStage.allowGenerateNextVisit())
                .validCompleteOnly(programStage.validCompleteOnly())
                .reportDateToUse(programStage.reportDateToUse())
                .openAfterEnrollment(programStage.openAfterEnrollment())
                .repeatable(programStage.repeatable())
                .captureCoordinates(programStage.captureCoordinates())
                .formType(programStage.formType())
                .displayGenerateEventBox(programStage.displayGenerateEventBox())
                .generatedByEnrollmentDate(programStage.generatedByEnrollmentDate())
                .autoGenerateEvent(programStage.autoGenerateEvent())
                .sortOrder(programStage.sortOrder())
                .hideDueDate(programStage.hideDueDate())
                .blockEntryForm(programStage.blockEntryForm())
                .minDaysFromStart(programStage.minDaysFromStart())
                .standardInterval(programStage.standardInterval())
                .build();
    }
}
