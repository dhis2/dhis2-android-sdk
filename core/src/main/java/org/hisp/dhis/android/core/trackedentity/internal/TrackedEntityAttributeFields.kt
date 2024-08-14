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
package org.hisp.dhis.android.core.trackedentity.internal

import org.hisp.dhis.android.core.arch.api.fields.internal.BaseFields
import org.hisp.dhis.android.core.arch.api.fields.internal.Field
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.internal.AccessFields
import org.hisp.dhis.android.core.common.objectstyle.internal.ObjectStyleFields
import org.hisp.dhis.android.core.legendset.LegendSet
import org.hisp.dhis.android.core.legendset.internal.LegendSetFields
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeTableInfo

internal object TrackedEntityAttributeFields : BaseFields<TrackedEntityAttribute>() {
    const val UNIQUE = "unique"
    const val LEGEND_SETS = "legendSets"
    private const val STYLE = "style"
    private const val ACCESS = "access"
    const val ORG_UNIT_SCOPE = "orgunitScope"

    val uid: Field<TrackedEntityAttribute> = fh.uid()
    val lastUpdated: Field<TrackedEntityAttribute> = fh.lastUpdated()

    val allFields = Fields.from(
        fh.getNameableFields(),
        fh.field(TrackedEntityAttributeTableInfo.Columns.PATTERN),
        fh.field(TrackedEntityAttributeTableInfo.Columns.SORT_ORDER_IN_LIST_NO_PROGRAM),
        fh.field(TrackedEntityAttributeTableInfo.Columns.VALUE_TYPE),
        fh.field(TrackedEntityAttributeTableInfo.Columns.EXPRESSION),
        fh.field(TrackedEntityAttributeTableInfo.Columns.PROGRAM_SCOPE),
        fh.field(TrackedEntityAttributeTableInfo.Columns.AGGREGATION_TYPE),
        fh.field(TrackedEntityAttributeTableInfo.Columns.DISPLAY_IN_LIST_NO_PROGRAM),
        fh.field(TrackedEntityAttributeTableInfo.Columns.GENERATED),
        fh.field(TrackedEntityAttributeTableInfo.Columns.DISPLAY_ON_VISIT_SCHEDULE),
        fh.field(TrackedEntityAttributeTableInfo.Columns.ORG_UNIT_SCOPE),
        fh.field(UNIQUE),
        fh.field(TrackedEntityAttributeTableInfo.Columns.INHERIT),
        fh.field(TrackedEntityAttributeTableInfo.Columns.FIELD_MASK),
        fh.nestedField<LegendSet>(LEGEND_SETS).with(LegendSetFields.uid),
        fh.nestedFieldWithUid(TrackedEntityAttributeTableInfo.Columns.OPTION_SET),
        fh.nestedField<ObjectStyle>(STYLE).with(ObjectStyleFields.allFields),
        fh.nestedField<Access>(ACCESS).with(AccessFields.read),
        fh.field(TrackedEntityAttributeTableInfo.Columns.FORM_NAME),
        fh.field(TrackedEntityAttributeTableInfo.Columns.DISPLAY_FORM_NAME),
        fh.field(TrackedEntityAttributeTableInfo.Columns.CONFIDENTIAL),
    )
}
