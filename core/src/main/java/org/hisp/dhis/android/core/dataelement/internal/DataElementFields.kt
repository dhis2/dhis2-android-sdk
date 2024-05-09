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
package org.hisp.dhis.android.core.dataelement.internal

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper
import org.hisp.dhis.android.core.attribute.AttributeValue
import org.hisp.dhis.android.core.attribute.internal.AttributeValuesFields
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.common.internal.AccessFields
import org.hisp.dhis.android.core.common.objectstyle.internal.ObjectStyleFields
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.DataElementTableInfo
import org.hisp.dhis.android.core.legendset.LegendSet
import org.hisp.dhis.android.core.legendset.internal.LegendSetFields

internal object DataElementFields {
    private const val STYLE = "style"
    private const val ACCESS = "access"
    const val LEGEND_SETS = "legendSets"
    const val ATTRIBUTE_VALUES = "attributeValues"

    private val fh = FieldsHelper<DataElement>()
    val uid = fh.uid()

    val lastUpdated = fh.lastUpdated()

    val allFields: Fields<DataElement> = Fields.builder<DataElement>()
        .fields(fh.getNameableFields())
        .fields(
            fh.field<ValueType>(DataElementTableInfo.Columns.VALUE_TYPE),
            fh.field<Boolean>(DataElementTableInfo.Columns.ZERO_IS_SIGNIFICANT),
            fh.field<String>(DataElementTableInfo.Columns.AGGREGATION_TYPE),
            fh.field<String>(DataElementTableInfo.Columns.FORM_NAME),
            fh.field<String>(DataElementTableInfo.Columns.DOMAIN_TYPE),
            fh.field<String>(DataElementTableInfo.Columns.DISPLAY_FORM_NAME),
            fh.nestedField<ObjectWithUid>(DataElementTableInfo.Columns.OPTION_SET)
                .with(ObjectWithUid.uid),
            fh.nestedField<ObjectWithUid>(DataElementTableInfo.Columns.CATEGORY_COMBO)
                .with(ObjectWithUid.uid),
            fh.field<String>(DataElementTableInfo.Columns.FIELD_MASK),
            fh.nestedField<ObjectStyle>(STYLE)
                .with(ObjectStyleFields.allFields),
            fh.nestedField<Access>(ACCESS)
                .with(AccessFields.read),
            fh.nestedField<LegendSet>(LEGEND_SETS).with(LegendSetFields.uid),
            fh.nestedField<AttributeValue>(ATTRIBUTE_VALUES).with(AttributeValuesFields.allFields),
        ).build()
}
