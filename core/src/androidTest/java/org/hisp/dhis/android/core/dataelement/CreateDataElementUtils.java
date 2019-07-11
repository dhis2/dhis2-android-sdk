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

package org.hisp.dhis.android.core.dataelement;

import android.content.ContentValues;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.internal.DataElementFields;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CreateDataElementUtils {
    private static final long ID = 2L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";
    private static final ValueType VALUE_TYPE = ValueType.TEXT;
    private static final Integer ZERO_IS_SIGNIFICANT = 0;
    private static final String AGGREGATION_OPERATOR = "test_aggregationOperator";
    private static final String FORM_NAME = "test_formName";
    private static final String NUMBER_TYPE = "test_numberType";
    private static final String DOMAIN_TYPE = "test_domainType";
    private static final String DIMENSION = "test_dimension";
    private static final String DISPLAY_FORM_NAME = "test_displayFormName";

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";

    public static ContentValues create(long id, @NonNull String uid, @Nullable String optionSetId) {
        ContentValues dataElement = new ContentValues();
        dataElement.put(DataElementTableInfo.Columns.ID, id);
        dataElement.put(DataElementTableInfo.Columns.UID, uid);
        dataElement.put(DataElementTableInfo.Columns.CODE, CODE);
        dataElement.put(DataElementTableInfo.Columns.NAME, NAME);
        dataElement.put(DataElementTableInfo.Columns.DISPLAY_NAME, DISPLAY_NAME);
        dataElement.put(DataElementTableInfo.Columns.CREATED, DATE);
        dataElement.put(DataElementTableInfo.Columns.LAST_UPDATED, DATE);
        dataElement.put(DataElementTableInfo.Columns.SHORT_NAME, SHORT_NAME);
        dataElement.put(DataElementTableInfo.Columns.DISPLAY_SHORT_NAME, DISPLAY_SHORT_NAME);
        dataElement.put(DataElementTableInfo.Columns.DESCRIPTION, DESCRIPTION);
        dataElement.put(DataElementTableInfo.Columns.DISPLAY_DESCRIPTION, DISPLAY_DESCRIPTION);
        dataElement.put(DataElementFields.VALUE_TYPE, VALUE_TYPE.name());
        dataElement.put(DataElementFields.ZERO_IS_SIGNIFICANT, ZERO_IS_SIGNIFICANT);
        dataElement.put(DataElementFields.AGGREGATION_TYPE, AGGREGATION_OPERATOR);
        dataElement.put(DataElementFields.FORM_NAME, FORM_NAME);
        dataElement.put(DataElementFields.NUMBER_TYPE, NUMBER_TYPE);
        dataElement.put(DataElementFields.DOMAIN_TYPE, DOMAIN_TYPE);
        dataElement.put(DataElementFields.DIMENSION, DIMENSION);
        dataElement.put(DataElementFields.DISPLAY_FORM_NAME, DISPLAY_FORM_NAME);
        dataElement.put(DataElementFields.CATEGORY_COMBO, CategoryCombo.DEFAULT_UID);
        if (optionSetId == null) {
            dataElement.putNull(DataElementFields.OPTION_SET);
        } else {
            dataElement.put(DataElementFields.OPTION_SET, optionSetId);
        }

        return dataElement;
    }
}