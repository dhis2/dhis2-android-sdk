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

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.DataElementModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toBoolean;

@RunWith(AndroidJUnit4.class)
public class DataElementModelShould {
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
    private static final String AGGREGATION_TYPE = "test_aggregationOperator";
    private static final String FORM_NAME = "test_formName";
    private static final String NUMBER_TYPE = "test_numberType";
    private static final String DOMAIN_TYPE = "test_domainType";
    private static final String DIMENSION = "test_dimension";
    private static final String DISPLAY_FORM_NAME = "test_displayFormName";
    private static final String OPTION_SET = "test_optionSet";
    private static final String CATEGORY_COMBO = "test_categoryCombo";

    private final Date date;
    private final String dateString;

    public DataElementModelShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }
    @Test
    @SmallTest
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                Columns.ID,
                Columns.UID,
                Columns.CODE,
                Columns.NAME,
                Columns.DISPLAY_NAME,
                Columns.CREATED,
                Columns.LAST_UPDATED,
                Columns.SHORT_NAME,
                Columns.DISPLAY_SHORT_NAME,
                Columns.DESCRIPTION,
                Columns.DISPLAY_DESCRIPTION,
                Columns.VALUE_TYPE,
                Columns.ZERO_IS_SIGNIFICANT,
                Columns.AGGREGATION_TYPE,
                Columns.FORM_NAME,
                Columns.NUMBER_TYPE,
                Columns.DOMAIN_TYPE,
                Columns.DIMENSION,
                Columns.DISPLAY_FORM_NAME,
                Columns.OPTION_SET,
                Columns.CATEGORY_COMBO
        });
        cursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME,
                dateString, dateString,
                SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION,
                VALUE_TYPE, ZERO_IS_SIGNIFICANT, AGGREGATION_TYPE,
                FORM_NAME, NUMBER_TYPE, DOMAIN_TYPE, DIMENSION,
                DISPLAY_FORM_NAME, OPTION_SET, CATEGORY_COMBO
        });
        cursor.moveToFirst();

        DataElementModel model = DataElementModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.uid()).isEqualTo(UID);
        assertThat(model.code()).isEqualTo(CODE);
        assertThat(model.name()).isEqualTo(NAME);
        assertThat(model.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(model.created()).isEqualTo(date);
        assertThat(model.lastUpdated()).isEqualTo(date);
        assertThat(model.shortName()).isEqualTo(SHORT_NAME);
        assertThat(model.displayShortName()).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(model.description()).isEqualTo(DESCRIPTION);
        assertThat(model.displayDescription()).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(model.valueType()).isEqualTo(VALUE_TYPE);
        assertThat(model.zeroIsSignificant()).isFalse();
        assertThat(model.aggregationType()).isEqualTo(AGGREGATION_TYPE);
        assertThat(model.formName()).isEqualTo(FORM_NAME);
        assertThat(model.numberType()).isEqualTo(NUMBER_TYPE);
        assertThat(model.domainType()).isEqualTo(DOMAIN_TYPE);
        assertThat(model.dimension()).isEqualTo(DIMENSION);
        assertThat(model.displayFormName()).isEqualTo(DISPLAY_FORM_NAME);
        assertThat(model.optionSet()).isEqualTo(OPTION_SET);
        assertThat(model.categoryCombo()).isEqualTo(CATEGORY_COMBO);
    }

    @Test
    @SmallTest
    public void create_content_values_when_created_from_builder() {
        DataElementModel model = DataElementModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(date)
                .lastUpdated(date)
                .shortName(SHORT_NAME)
                .displayShortName(DISPLAY_SHORT_NAME)
                .description(DESCRIPTION)
                .displayDescription(DISPLAY_DESCRIPTION)
                .valueType(VALUE_TYPE)
                .zeroIsSignificant(toBoolean(ZERO_IS_SIGNIFICANT))
                .aggregationType(AGGREGATION_TYPE)
                .formName(FORM_NAME)
                .numberType(NUMBER_TYPE)
                .domainType(DOMAIN_TYPE)
                .dimension(DIMENSION)
                .displayFormName(DISPLAY_FORM_NAME)
                .optionSet(OPTION_SET)
                .categoryCombo(CATEGORY_COMBO)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.SHORT_NAME)).isEqualTo(SHORT_NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_SHORT_NAME)).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(contentValues.getAsString(Columns.DESCRIPTION)).isEqualTo(DESCRIPTION);
        assertThat(contentValues.getAsString(Columns.DISPLAY_DESCRIPTION)).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(contentValues.getAsString(Columns.VALUE_TYPE)).isEqualTo(VALUE_TYPE.name());
        assertThat(contentValues.getAsBoolean(Columns.ZERO_IS_SIGNIFICANT)).isFalse();
        assertThat(contentValues.getAsString(Columns.AGGREGATION_TYPE)).isEqualTo(AGGREGATION_TYPE);
        assertThat(contentValues.getAsString(Columns.FORM_NAME)).isEqualTo(FORM_NAME);
        assertThat(contentValues.getAsString(Columns.DOMAIN_TYPE)).isEqualTo(DOMAIN_TYPE);
        assertThat(contentValues.getAsString(Columns.DIMENSION)).isEqualTo(DIMENSION);
        assertThat(contentValues.getAsString(Columns.DISPLAY_FORM_NAME)).isEqualTo(DISPLAY_FORM_NAME);
        assertThat(contentValues.getAsString(Columns.OPTION_SET)).isEqualTo(OPTION_SET);
        assertThat(contentValues.getAsString(Columns.CATEGORY_COMBO)).isEqualTo(CATEGORY_COMBO);
    }
}
