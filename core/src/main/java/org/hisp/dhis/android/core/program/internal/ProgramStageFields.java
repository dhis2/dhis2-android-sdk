/*
 *  Copyright (c) 2004-2022, University of Oslo
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

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.attribute.AttributeValue;
import org.hisp.dhis.android.core.attribute.internal.AttributeValuesFields;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.internal.AccessFields;
import org.hisp.dhis.android.core.common.internal.DataAccessFields;
import org.hisp.dhis.android.core.common.objectstyle.internal.ObjectStyleFields;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageSection;
import org.hisp.dhis.android.core.program.ProgramStageTableInfo.Columns;

public final class ProgramStageFields {

    private static final String PROGRAM_STAGE_DATA_ELEMENTS = "programStageDataElements";
    private static final String CAPTURE_COORDINATES = "captureCoordinates";
    private static final String STYLE = "style";
    static final String PROGRAM_STAGE_SECTIONS = "programStageSections";
    private static final String ATTRIBUTE_VALUES = "attributeValues";
    private static final String ACCESS = "access";

    private static FieldsHelper<ProgramStage> fh = new FieldsHelper<>();

    static final Field<ProgramStage, String> uid = fh.uid();
    static final Fields<ProgramStage> allFields = Fields.<ProgramStage>builder()
            .fields(fh.getIdentifiableFields())
            .fields(
                    fh.<String>field(Columns.DESCRIPTION),
                    fh.<String>field(Columns.DISPLAY_DESCRIPTION),
                    fh.<String>field(Columns.EXECUTION_DATE_LABEL),
                    fh.<String>field(Columns.DUE_DATE_LABEL),
                    fh.<Boolean>field(Columns.ALLOW_GENERATE_NEXT_VISIT),
                    fh.<Boolean>field(Columns.VALID_COMPLETE_ONLY),
                    fh.<String>field(Columns.REPORT_DATE_TO_USE),
                    fh.<Boolean>field(Columns.OPEN_AFTER_ENROLLMENT),
                    fh.<Boolean>field(Columns.REPEATABLE),
                    fh.<Boolean>field(CAPTURE_COORDINATES),
                    fh.<FeatureType>field(Columns.FEATURE_TYPE),
                    fh.<FormType>field(Columns.FORM_TYPE),
                    fh.<Boolean>field(Columns.DISPLAY_GENERATE_EVENT_BOX),
                    fh.<Boolean>field(Columns.GENERATED_BY_ENROLMENT_DATE),
                    fh.<Boolean>field(Columns.AUTO_GENERATE_EVENT),
                    fh.<Integer>field(Columns.SORT_ORDER),
                    fh.<Boolean>field(Columns.HIDE_DUE_DATE),
                    fh.<Boolean>field(Columns.BLOCK_ENTRY_FORM),
                    fh.<Integer>field(Columns.MIN_DAYS_FROM_START),
                    fh.<Integer>field(Columns.STANDARD_INTERVAL),
                    fh.<ProgramStageSection>nestedField(PROGRAM_STAGE_SECTIONS)
                            .with(ProgramStageSectionFields.allFields),
                    fh.<ProgramStageDataElement>nestedField(PROGRAM_STAGE_DATA_ELEMENTS)
                            .with(ProgramStageDataElementFields.allFields),
                    fh.<ObjectStyle>nestedField(STYLE).with(ObjectStyleFields.allFields),
                    fh.<PeriodType>field(Columns.PERIOD_TYPE),
                    fh.<ObjectWithUid>field(Columns.PROGRAM),
                    fh.<Access>nestedField(ACCESS).with(AccessFields.data.with(DataAccessFields.write)),
                    fh.<Boolean>field(Columns.REMIND_COMPLETED),
                    fh.<Boolean>field(Columns.ENABLE_USER_ASSIGNMENT),
                    fh.<AttributeValue>nestedField(ATTRIBUTE_VALUES).with(AttributeValuesFields.allFields)
            ).build();

    private ProgramStageFields() {
    }
}