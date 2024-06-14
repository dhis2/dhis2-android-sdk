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
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.internal.AccessFields
import org.hisp.dhis.android.core.common.internal.DataAccessFields
import org.hisp.dhis.android.core.common.objectstyle.internal.ObjectStyleFields
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramRuleVariable
import org.hisp.dhis.android.core.program.ProgramSection
import org.hisp.dhis.android.core.program.ProgramTableInfo
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute

internal object ProgramFields {
    private const val PROGRAM_TRACKED_ENTITY_ATTRIBUTES = "programTrackedEntityAttributes"
    private const val CAPTURE_COORDINATES = "captureCoordinates"
    private const val PROGRAM_RULE_VARIABLES = "programRuleVariables"
    private const val ACCESS = "access"
    private const val STYLE = "style"
    private const val PROGRAM_SECTIONS = "programSections"
    const val ATTRIBUTE_VALUES = "attributeValues"

    private val fh = FieldsHelper<Program>()

    val uid = fh.uid()

    val allFields: Fields<Program> = Fields.builder<Program>()
        .fields(fh.getNameableFields())
        .fields(
            fh.field(ProgramTableInfo.Columns.VERSION),
            fh.field(ProgramTableInfo.Columns.ONLY_ENROLL_ONCE),
            fh.field(ProgramTableInfo.Columns.ENROLLMENT_DATE_LABEL),
            fh.field(ProgramTableInfo.Columns.DISPLAY_INCIDENT_DATE),
            fh.field(ProgramTableInfo.Columns.INCIDENT_DATE_LABEL),
            fh.field(ProgramTableInfo.Columns.REGISTRATION),
            fh.field(ProgramTableInfo.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE),
            fh.field(ProgramTableInfo.Columns.DATA_ENTRY_METHOD),
            fh.field(ProgramTableInfo.Columns.IGNORE_OVERDUE_EVENTS),
            fh.field(ProgramTableInfo.Columns.SELECT_INCIDENT_DATES_IN_FUTURE),
            fh.field(CAPTURE_COORDINATES),
            fh.field(ProgramTableInfo.Columns.USE_FIRST_STAGE_DURING_REGISTRATION),
            fh.field(ProgramTableInfo.Columns.DISPLAY_FRONT_PAGE_LIST),
            fh.field(ProgramTableInfo.Columns.PROGRAM_TYPE),
            fh.nestedField<ProgramTrackedEntityAttribute>(PROGRAM_TRACKED_ENTITY_ATTRIBUTES).with(
                ProgramTrackedEntityAttributeFields.allFields,
            ),
            fh.nestedFieldWithUid(ProgramTableInfo.Columns.RELATED_PROGRAM),
            fh.nestedFieldWithUid(ProgramTableInfo.Columns.TRACKED_ENTITY_TYPE),
            fh.nestedFieldWithUid(ProgramTableInfo.Columns.CATEGORY_COMBO),
            fh.nestedField<Access>(ACCESS).with(AccessFields.data.with(DataAccessFields.write)),
            fh.nestedField<ProgramRuleVariable>(PROGRAM_RULE_VARIABLES)
                .with(ProgramRuleVariableFields.allFields),
            fh.nestedField<ObjectStyle>(STYLE).with(ObjectStyleFields.allFields),
            fh.field(ProgramTableInfo.Columns.EXPIRY_DAYS),
            fh.field(ProgramTableInfo.Columns.COMPLETE_EVENTS_EXPIRY_DAYS),
            fh.field(ProgramTableInfo.Columns.EXPIRY_PERIOD_TYPE),
            fh.field(ProgramTableInfo.Columns.MIN_ATTRIBUTES_REQUIRED_TO_SEARCH),
            fh.field(ProgramTableInfo.Columns.MAX_TEI_COUNT_TO_RETURN),
            fh.field(ProgramTableInfo.Columns.FEATURE_TYPE),
            fh.field(ProgramTableInfo.Columns.ACCESS_LEVEL),
            fh.nestedField<ProgramSection>(PROGRAM_SECTIONS).with(ProgramSectionFields.allFields),
            fh.nestedField<AttributeValue>(ATTRIBUTE_VALUES).with(AttributeValuesFields.allFields),
            fh.field(ProgramTableInfo.Columns.ENROLLMENT_LABEL),
            fh.field(ProgramTableInfo.Columns.FOLLOW_UP_LABEL),
            fh.field(ProgramTableInfo.Columns.ORG_UNIT_LABEL),
            fh.field(ProgramTableInfo.Columns.RELATIONSHIP_LABEL),
            fh.field(ProgramTableInfo.Columns.NOTE_LABEL),
            fh.field(ProgramTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE_LABEL),
            fh.field(ProgramTableInfo.Columns.PROGRAM_STAGE_LABEL),
            fh.field(ProgramTableInfo.Columns.EVENT_LABEL),
        ).build()
}
