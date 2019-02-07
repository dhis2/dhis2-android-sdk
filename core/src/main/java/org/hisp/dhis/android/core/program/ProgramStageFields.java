/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.core.arch.fields.FieldsHelper;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleFields;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.period.PeriodType;

final class ProgramStageFields {

    final static String DESCRIPTION = "description";
    final static String DISPLAY_DESCRIPTION = "displayDescription";
    static final String EXECUTION_DATE_LABEL = "executionDateLabel";
    static final String ALLOW_GENERATE_NEXT_VISIT = "allowGenerateNextVisit";
    static final String VALID_COMPLETE_ONLY = "validCompleteOnly";
    static final String REPORT_DATE_TO_USE = "reportDateToUse";
    static final String OPEN_AFTER_ENROLLMENT = "openAfterEnrollment";
    private static final String PROGRAM_STAGE_DATA_ELEMENTS = "programStageDataElements";
    static final String REPEATABLE = "repeatable";
    /**
     * @deprecated since 2.29, replaced by {@link #FEATURE_TYPE}
     */
    @Deprecated
    static final String CAPTURE_COORDINATES = "captureCoordinates";
    static final String FEATURE_TYPE = "featureType";
    static final String FORM_TYPE = "formType";
    static final String DISPLAY_GENERATE_EVENT_BOX = "displayGenerateEventBox";
    static final String GENERATED_BY_ENROLMENT_DATE = "generatedByEnrollmentDate";
    static final String AUTO_GENERATE_EVENT = "autoGenerateEvent";
    static final String SORT_ORDER = "sortOrder";
    static final String HIDE_DUE_DATE = "hideDueDate";
    static final String BLOCK_ENTRY_FORM = "blockEntryForm";
    static final String MIN_DAYS_FROM_START = "minDaysFromStart";
    static final String STANDARD_INTERVAL = "standardInterval";
    private static final String PROGRAM_STAGE_SECTIONS = "programStageSections";
    private static final String STYLE = "style";
    static final String PERIOD_TYPE = "periodType";
    static final String PROGRAM = "program";
    private final static String ACCESS = "access";
    final static String REMIND_COMPLETED = "remindCompleted";


    private static FieldsHelper<ProgramStage> fh = new FieldsHelper<>();

    final static Field<ProgramStage, String> uid = fh.uid();
    final static Fields<ProgramStage> allFields = Fields.<ProgramStage>builder()
            .fields(fh.getIdentifiableFields())
            .fields(
                    fh.<String>field(DESCRIPTION),
                    fh.<String>field(DISPLAY_DESCRIPTION),
                    fh.<String>field(EXECUTION_DATE_LABEL),
                    fh.<Boolean>field(ALLOW_GENERATE_NEXT_VISIT),
                    fh.<Boolean>field(VALID_COMPLETE_ONLY),
                    fh.<String>field(REPORT_DATE_TO_USE),
                    fh.<Boolean>field(OPEN_AFTER_ENROLLMENT),
                    fh.<Boolean>field(REPEATABLE),
                    fh.<Boolean>field(CAPTURE_COORDINATES),
                    fh.<FeatureType>field(FEATURE_TYPE),
                    fh.<FormType>field(FORM_TYPE),
                    fh.<Boolean>field(DISPLAY_GENERATE_EVENT_BOX),
                    fh.<Boolean>field(GENERATED_BY_ENROLMENT_DATE),
                    fh.<Boolean>field(AUTO_GENERATE_EVENT),
                    fh.<Integer>field(SORT_ORDER),
                    fh.<Boolean>field(HIDE_DUE_DATE),
                    fh.<Boolean>field(BLOCK_ENTRY_FORM),
                    fh.<Integer>field(MIN_DAYS_FROM_START),
                    fh.<Integer>field(STANDARD_INTERVAL),
                    fh.<ProgramStageSection>nestedField(PROGRAM_STAGE_SECTIONS)
                            .with(ProgramStageSectionFields.allFields),
                    fh.<ProgramStageDataElement>nestedField(PROGRAM_STAGE_DATA_ELEMENTS)
                            .with(ProgramStageDataElementFields.allFields),
                    fh.<ObjectStyle>nestedField(STYLE).with(ObjectStyleFields.allFields),
                    fh.<PeriodType>field(PERIOD_TYPE),
                    fh.<ObjectWithUid>field(PROGRAM),
                    fh.<Access>nestedField(ACCESS).with(Access.data.with(DataAccess.write)),
                    fh.<Boolean>field(REMIND_COMPLETED)
            ).build();


    private ProgramStageFields() {
    }
}