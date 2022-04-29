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

package org.hisp.dhis.android.core.dataelement.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.attribute.AttributeValue;
import org.hisp.dhis.android.core.attribute.internal.AttributeValuesFields;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.common.internal.AccessFields;
import org.hisp.dhis.android.core.common.objectstyle.internal.ObjectStyleFields;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementTableInfo;
import org.hisp.dhis.android.core.legendset.LegendSet;
import org.hisp.dhis.android.core.legendset.internal.LegendSetFields;

public final class DataElementFields {

    private final static String STYLE = "style";
    private final static String ACCESS = "access";
    public static final String LEGEND_SETS = "legendSets";
    private static final String ATTRIBUTE_VALUES = "attributeValues";

    private static final FieldsHelper<DataElement> fh = new FieldsHelper<>();

    public static final Field<DataElement, String> uid = fh.uid();

    static final Field<DataElement, String> lastUpdated = fh.lastUpdated();

    public static final Fields<DataElement> allFields = Fields.<DataElement>builder()
            .fields(fh.getNameableFields())
            .fields(
                    fh.<ValueType>field(DataElementTableInfo.Columns.VALUE_TYPE),
                    fh.<Boolean>field(DataElementTableInfo.Columns.ZERO_IS_SIGNIFICANT),
                    fh.<String>field(DataElementTableInfo.Columns.AGGREGATION_TYPE),
                    fh.<String>field(DataElementTableInfo.Columns.FORM_NAME),
                    fh.<String>field(DataElementTableInfo.Columns.DOMAIN_TYPE),
                    fh.<String>field(DataElementTableInfo.Columns.DISPLAY_FORM_NAME),
                    fh.<ObjectWithUid>nestedField(DataElementTableInfo.Columns.OPTION_SET)
                            .with(ObjectWithUid.uid),
                    fh.<ObjectWithUid>nestedField(DataElementTableInfo.Columns.CATEGORY_COMBO)
                            .with(ObjectWithUid.uid),
                    fh.<String>field(DataElementTableInfo.Columns.FIELD_MASK),
                    fh.<ObjectStyle>nestedField(STYLE)
                            .with(ObjectStyleFields.allFields),
                    fh.<Access>nestedField(ACCESS)
                            .with(AccessFields.read),
                    fh.<LegendSet>nestedField(LEGEND_SETS).with(LegendSetFields.uid),
                    fh.<AttributeValue>nestedField(ATTRIBUTE_VALUES).with(AttributeValuesFields.allFields)
            ).build();

    private DataElementFields() {
    }
}