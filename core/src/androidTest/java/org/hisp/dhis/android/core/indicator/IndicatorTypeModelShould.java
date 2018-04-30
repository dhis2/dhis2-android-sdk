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

package org.hisp.dhis.android.core.indicator;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.IdentifiableModelAbstractShould;
import org.hisp.dhis.android.core.indicator.IndicatorTypeModel.Columns;
import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CODE;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DELETED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DISPLAY_NAME;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.LAST_UPDATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.NAME;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.UID;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillIdentifiableModelProperties;

@RunWith(AndroidJUnit4.class)
public class IndicatorTypeModelShould extends IdentifiableModelAbstractShould<IndicatorType, IndicatorTypeModel> {

    private final Boolean number = false;
    private final Integer factor = 100;

    public IndicatorTypeModelShould() {
            super(new Columns().all(), 8, new IndicatorTypeModelBuilder());
    }

    @Override
    protected IndicatorTypeModel buildModel() {
        IndicatorTypeModel.Builder builder = IndicatorTypeModel.builder();
        fillIdentifiableModelProperties(builder);
        builder
                .number(number)
                .factor(factor);
        return builder.build();
    }

    @Override
    protected IndicatorTypeModel cursorToModel(Cursor cursor) {
        return IndicatorTypeModel.create(cursor);
    }

    @Override
    protected IndicatorType buildPojo() {
        return IndicatorType.create(UID, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED,
                number, factor, DELETED);
    }

    @Override
    protected Object[] getModelAsObjectArray() {
        return Utils.appendInNewArray(ColumnsArrayUtils.getIdentifiableModelAsObjectArray(model),
                model.number(), model.factor());
    }

    @Test
    public void have_extra_indicator_model_columns() {
        List<String> columnsList = Arrays.asList(columns);

        assertThat(columnsList.contains(Columns.NUMBER)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.FACTOR)).isEqualTo(true);
    }
}