/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.network.programstage

import org.hisp.dhis.android.core.attribute.AttributeValue
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramStageDataElement
import org.hisp.dhis.android.core.program.ProgramStageSection
import org.hisp.dhis.android.network.attribute.AttributeValueFields
import org.hisp.dhis.android.network.common.fields.AccessFields
import org.hisp.dhis.android.network.common.fields.BaseFields
import org.hisp.dhis.android.network.common.fields.DataAccessFields
import org.hisp.dhis.android.network.common.fields.Fields
import org.hisp.dhis.android.network.common.fields.ObjectStyleFields
import org.hisp.dhis.android.persistence.program.ProgramStageTableInfo.Columns

internal object ProgramStageFields : BaseFields<ProgramStage>() {
    private const val PROGRAM_STAGE_DATA_ELEMENTS = "programStageDataElements"
    private const val CAPTURE_COORDINATES = "captureCoordinates"
    private const val STYLE = "style"
    private const val PROGRAM_STAGE_SECTIONS = "programStageSections"
    const val ATTRIBUTE_VALUES = "attributeValues"
    private const val ACCESS = "access"

    val uid = fh.uid()

    val allFields = Fields.from(
        fh.getIdentifiableFields(),
        fh.field(Columns.DESCRIPTION),
        fh.field(Columns.DISPLAY_DESCRIPTION),
        fh.field(Columns.DISPLAY_EXECUTION_DATE_LABEL),
        fh.field(Columns.DISPLAY_DUE_DATE_LABEL),
        fh.field(Columns.ALLOW_GENERATE_NEXT_VISIT),
        fh.field(Columns.VALID_COMPLETE_ONLY),
        fh.field(Columns.REPORT_DATE_TO_USE),
        fh.field(Columns.OPEN_AFTER_ENROLLMENT),
        fh.field(Columns.REPEATABLE),
        fh.field(CAPTURE_COORDINATES),
        fh.field(Columns.FEATURE_TYPE),
        fh.field(Columns.FORM_TYPE),
        fh.field(Columns.DISPLAY_GENERATE_EVENT_BOX),
        fh.field(Columns.GENERATED_BY_ENROLLMENT_DATE),
        fh.field(Columns.AUTO_GENERATE_EVENT),
        fh.field(Columns.SORT_ORDER),
        fh.field(Columns.HIDE_DUE_DATE),
        fh.field(Columns.BLOCK_ENTRY_FORM),
        fh.field(Columns.MIN_DAYS_FROM_START),
        fh.field(Columns.STANDARD_INTERVAL),
        fh.field(Columns.PERIOD_TYPE),
        fh.field(Columns.PROGRAM),
        fh.field(Columns.REMIND_COMPLETED),
        fh.field(Columns.VALIDATION_STRATEGY),
        fh.field(Columns.ENABLE_USER_ASSIGNMENT),
        fh.field(Columns.DISPLAY_PROGRAM_STAGE_LABEL),
        fh.field(Columns.DISPLAY_EVENT_LABEL),
        fh.nestedField<ProgramStageSection>(PROGRAM_STAGE_SECTIONS).with(ProgramStageSectionFields.allFields),
        fh.nestedField<ProgramStageDataElement>(PROGRAM_STAGE_DATA_ELEMENTS)
            .with(ProgramStageDataElementFields.allFields),
        fh.nestedField<ObjectStyle>(STYLE).with(ObjectStyleFields.allFields),
        fh.nestedField<AttributeValue>(ATTRIBUTE_VALUES).with(AttributeValueFields.allFields),
        fh.nestedField<Access>(ACCESS).with(AccessFields.data.with(DataAccessFields.write)),
    )
}
