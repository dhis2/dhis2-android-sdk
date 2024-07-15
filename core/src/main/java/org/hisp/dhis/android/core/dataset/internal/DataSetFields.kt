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
package org.hisp.dhis.android.core.dataset.internal

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.internal.AccessFields
import org.hisp.dhis.android.core.common.internal.DataAccessFields
import org.hisp.dhis.android.core.common.objectstyle.internal.ObjectStyleFields
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.dataelement.internal.DataElementOperandFields
import org.hisp.dhis.android.core.dataset.DataInputPeriod
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetElement
import org.hisp.dhis.android.core.dataset.DataSetTableInfo
import org.hisp.dhis.android.core.dataset.Section
import org.hisp.dhis.android.core.period.PeriodType

internal object DataSetFields {
    const val DATA_SET_ELEMENTS = "dataSetElements"
    const val INDICATORS = "indicators"
    const val SECTIONS = "sections"
    const val COMPULSORY_DATA_ELEMENT_OPERANDS = "compulsoryDataElementOperands"
    const val DATA_INPUT_PERIODS = "dataInputPeriods"
    private const val ACCESS = "access"
    private const val STYLE = "style"

    private val fh = FieldsHelper<DataSet>()

    val uid = fh.uid()

    val allFields: Fields<DataSet> = Fields.builder<DataSet>()
        .fields(fh.getNameableFields())
        .fields(
            fh.field<PeriodType>(DataSetTableInfo.Columns.PERIOD_TYPE),
            fh.nestedFieldWithUid(DataSetTableInfo.Columns.CATEGORY_COMBO),
            fh.field<Boolean>(DataSetTableInfo.Columns.MOBILE),
            fh.field<Int>(DataSetTableInfo.Columns.VERSION),
            fh.field<Int>(DataSetTableInfo.Columns.EXPIRY_DAYS),
            fh.field<Int>(DataSetTableInfo.Columns.TIMELY_DAYS),
            fh.field<Boolean>(DataSetTableInfo.Columns.NOTIFY_COMPLETING_USER),
            fh.field<Int>(DataSetTableInfo.Columns.OPEN_FUTURE_PERIODS),
            fh.field<Boolean>(DataSetTableInfo.Columns.FIELD_COMBINATION_REQUIRED),
            fh.field<Boolean>(DataSetTableInfo.Columns.VALID_COMPLETE_ONLY),
            fh.field<Boolean>(DataSetTableInfo.Columns.NO_VALUE_REQUIRES_COMMENT),
            fh.field<Boolean>(DataSetTableInfo.Columns.SKIP_OFFLINE),
            fh.field<Boolean>(DataSetTableInfo.Columns.DATA_ELEMENT_DECORATION),
            fh.field<Boolean>(DataSetTableInfo.Columns.RENDER_AS_TABS),
            fh.field<Boolean>(DataSetTableInfo.Columns.RENDER_HORIZONTALLY),
            fh.nestedFieldWithUid(DataSetTableInfo.Columns.WORKFLOW),
            fh.nestedField<DataSetElement>(DATA_SET_ELEMENTS).with(DataSetElementFields.allFields),
            fh.nestedFieldWithUid(INDICATORS),
            fh.nestedField<Section>(SECTIONS).with(SectionFields.allFields),
            fh.nestedField<DataElementOperand>(COMPULSORY_DATA_ELEMENT_OPERANDS)
                .with(DataElementOperandFields.allFields),
            fh.nestedField<DataInputPeriod>(DATA_INPUT_PERIODS).with(DataInputPeriodFields.allFields),
            fh.nestedField<Access>(ACCESS).with(AccessFields.data.with(DataAccessFields.write)),
            fh.nestedField<ObjectStyle>(STYLE).with(ObjectStyleFields.allFields),
        ).build()
}
