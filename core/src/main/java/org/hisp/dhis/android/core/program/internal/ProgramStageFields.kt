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
package org.hisp.dhis.android.core.program.internal

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper
import org.hisp.dhis.android.core.attribute.AttributeValue
import org.hisp.dhis.android.core.attribute.internal.AttributeValuesFields
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.FormType
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValidationStrategy
import org.hisp.dhis.android.core.common.internal.AccessFields
import org.hisp.dhis.android.core.common.internal.DataAccessFields
import org.hisp.dhis.android.core.common.objectstyle.internal.ObjectStyleFields
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramStageDataElement
import org.hisp.dhis.android.core.program.ProgramStageSection
import org.hisp.dhis.android.core.program.ProgramStageTableInfo

internal object ProgramStageFields {
    private const val PROGRAM_STAGE_DATA_ELEMENTS = "programStageDataElements"
    private const val CAPTURE_COORDINATES = "captureCoordinates"
    private const val STYLE = "style"
    const val PROGRAM_STAGE_SECTIONS = "programStageSections"
    const val ATTRIBUTE_VALUES = "attributeValues"
    private const val ACCESS = "access"

    private val fh = FieldsHelper<ProgramStage>()

    val uid = fh.uid()

    val allFields: Fields<ProgramStage> = Fields.builder<ProgramStage>()
        .fields(fh.getIdentifiableFields())
        .fields(
            fh.field<String>(ProgramStageTableInfo.Columns.DESCRIPTION),
            fh.field<String>(ProgramStageTableInfo.Columns.DISPLAY_DESCRIPTION),
            fh.field<String>(ProgramStageTableInfo.Columns.EXECUTION_DATE_LABEL),
            fh.field<String>(ProgramStageTableInfo.Columns.DUE_DATE_LABEL),
            fh.field<Boolean>(ProgramStageTableInfo.Columns.ALLOW_GENERATE_NEXT_VISIT),
            fh.field<Boolean>(ProgramStageTableInfo.Columns.VALID_COMPLETE_ONLY),
            fh.field<String>(ProgramStageTableInfo.Columns.REPORT_DATE_TO_USE),
            fh.field<Boolean>(ProgramStageTableInfo.Columns.OPEN_AFTER_ENROLLMENT),
            fh.field<Boolean>(ProgramStageTableInfo.Columns.REPEATABLE),
            fh.field<Boolean>(CAPTURE_COORDINATES),
            fh.field<FeatureType>(ProgramStageTableInfo.Columns.FEATURE_TYPE),
            fh.field<FormType>(ProgramStageTableInfo.Columns.FORM_TYPE),
            fh.field<Boolean>(ProgramStageTableInfo.Columns.DISPLAY_GENERATE_EVENT_BOX),
            fh.field<Boolean>(ProgramStageTableInfo.Columns.GENERATED_BY_ENROLMENT_DATE),
            fh.field<Boolean>(ProgramStageTableInfo.Columns.AUTO_GENERATE_EVENT),
            fh.field<Int>(ProgramStageTableInfo.Columns.SORT_ORDER),
            fh.field<Boolean>(ProgramStageTableInfo.Columns.HIDE_DUE_DATE),
            fh.field<Boolean>(ProgramStageTableInfo.Columns.BLOCK_ENTRY_FORM),
            fh.field<Int>(ProgramStageTableInfo.Columns.MIN_DAYS_FROM_START),
            fh.field<Int>(ProgramStageTableInfo.Columns.STANDARD_INTERVAL),
            fh.nestedField<ProgramStageSection>(PROGRAM_STAGE_SECTIONS)
                .with(ProgramStageSectionFields.allFields),
            fh.nestedField<ProgramStageDataElement>(PROGRAM_STAGE_DATA_ELEMENTS)
                .with(ProgramStageDataElementFields.allFields),
            fh.nestedField<ObjectStyle>(STYLE).with(ObjectStyleFields.allFields),
            fh.field<PeriodType>(ProgramStageTableInfo.Columns.PERIOD_TYPE),
            fh.field<ObjectWithUid>(ProgramStageTableInfo.Columns.PROGRAM),
            fh.nestedField<Access>(ACCESS).with(AccessFields.data.with(DataAccessFields.write)),
            fh.field<Boolean>(ProgramStageTableInfo.Columns.REMIND_COMPLETED),
            fh.field<ValidationStrategy>(ProgramStageTableInfo.Columns.VALIDATION_STRATEGY),
            fh.field<Boolean>(ProgramStageTableInfo.Columns.ENABLE_USER_ASSIGNMENT),
            fh.nestedField<AttributeValue>(ATTRIBUTE_VALUES).with(AttributeValuesFields.allFields),
            fh.field<String>(ProgramStageTableInfo.Columns.PROGRAM_STAGE_LABEL),
            fh.field<String>(ProgramStageTableInfo.Columns.EVENT_LABEL),
        ).build()
}
