/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.dataelement;

import org.hisp.dhis.android.core.arch.fields.FieldsHelper;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.common.ValueTypeRendering;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;

public final class DataElementFields {

    final static String VALUE_TYPE = "valueType";
    final static String ZERO_IS_SIGNIFICANT = "zeroIsSignificant";
    final static String AGGREGATION_TYPE = "aggregationType";
    final static String FORM_NAME = "formName";
    final static String NUMBER_TYPE = "numberType";
    final static String DOMAIN_TYPE = "domainType";
    final static String DIMENSION = "dimension";
    final static String DISPLAY_FORM_NAME = "displayFormName";
    final static String OPTION_SET = "optionSet";
    final static String CATEGORY_COMBO = "categoryCombo";
    final static String STYLE = "style";
    final static String RENDER_TYPE = "renderType";
    final static String ACCESS = "access";

    private static final FieldsHelper<DataElement> fh = new FieldsHelper<>();

    public static final Field<DataElement, String> uid = fh.uid();

    static final Field<DataElement, String> lastUpdated = fh.lastUpdated();

    public static final Fields<DataElement> allFields = Fields.<DataElement>builder()
            .fields(fh.getNameableFields())
            .fields(
                    fh.<ValueType>field(VALUE_TYPE),
                    fh.<Boolean>field(ZERO_IS_SIGNIFICANT),
                    fh.<String>field(AGGREGATION_TYPE),
                    fh.<String>field(FORM_NAME),
                    fh.<String>field(NUMBER_TYPE),
                    fh.<String>field(DOMAIN_TYPE),
                    fh.<String>field(DIMENSION),
                    fh.<String>field(DISPLAY_FORM_NAME),
                    fh.<ObjectWithUid>nestedField(OPTION_SET)
                            .with(ObjectWithUid.uid),
                    fh.<ObjectWithUid>nestedField(CATEGORY_COMBO)
                            .with(ObjectWithUid.uid),
                    fh.<ObjectStyle>nestedField(STYLE)
                            .with(ObjectStyle.allFields),
                    fh.<ValueTypeRendering>nestedField(RENDER_TYPE),
                    fh.<Access>nestedField(ACCESS)
                            .with(Access.read)
            ).build();

    private DataElementFields() {
    }
}