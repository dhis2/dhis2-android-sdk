/*
 *  Copyright (c) 2004-2023, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.data.program;

import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillIdentifiableProperties;

import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ValidationStrategy;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramStage;

public class ProgramStageSamples {

    public static ProgramStage getProgramStage() {
        ProgramStage.Builder builder = ProgramStage.builder();

        fillIdentifiableProperties(builder);
        return builder
                .description("description")
                .displayDescription("display_description")
                .displayExecutionDateLabel("execution_date_label")
                .displayDueDateLabel("due_date_label")
                .allowGenerateNextVisit(Boolean.FALSE)
                .validCompleteOnly(Boolean.TRUE)
                .reportDateToUse("report_date_to_use")
                .openAfterEnrollment(Boolean.TRUE)
                .repeatable(Boolean.TRUE)
                .featureType(FeatureType.MULTI_POLYGON)
                .formType(FormType.CUSTOM)
                .displayGenerateEventBox(Boolean.FALSE)
                .generatedByEnrollmentDate(Boolean.TRUE)
                .autoGenerateEvent(Boolean.TRUE)
                .sortOrder(0)
                .hideDueDate(Boolean.FALSE)
                .blockEntryForm(Boolean.FALSE)
                .minDaysFromStart(2)
                .standardInterval(1)
                .periodType(PeriodType.BiMonthly)
                .program(ObjectWithUid.create("program_uid"))
                .access(Access.create(true, true, DataAccess.create(true, true)))
                .remindCompleted(Boolean.FALSE)
                .validationStrategy(ValidationStrategy.ON_UPDATE_AND_INSERT)
                .displayProgramStageLabel("programStageLabel")
                .displayEventLabel("eventLabel")
                .build();
    }

    public static ProgramStage getProgramStage(String name, Program program) {
        return getProgramStage().toBuilder()
                .uid(new UidGeneratorImpl().generate())
                .name(name)
                .program(ObjectWithUid.create(program.uid()))
                .build();
    }
}
