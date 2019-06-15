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

package org.hisp.dhis.android.core.indicator;

import android.database.Cursor;

import org.hisp.dhis.android.core.common.NameableModelAbstractShould;
import org.hisp.dhis.android.core.indicator.IndicatorModel.Columns;
import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillNameableModelProperties;

@RunWith(AndroidJUnit4.class)
public class IndicatorModelShould extends NameableModelAbstractShould<IndicatorModel> {

    public IndicatorModelShould() {
            super(new IndicatorModel.Columns().all(), 17);
    }

    @Override
    protected IndicatorModel buildModel() {
        IndicatorModel.Builder indicatorModelBuilder = IndicatorModel.builder();
        fillNameableModelProperties(indicatorModelBuilder);
        indicatorModelBuilder
                .annualized(false)
                .indicatorType("bWuNrMHEoZ0")
                .numerator("#{a.b}")
                .numeratorDescription("num descr")
                .denominator("#{c.d}")
                .denominatorDescription("den descr")
                .url("dhis2.org");
        return indicatorModelBuilder.build();
    }

    @Override
    protected IndicatorModel cursorToModel(Cursor cursor) {
        return IndicatorModel.create(cursor);
    }

    @Override
    protected Object[] getModelAsObjectArray() {
        return Utils.appendInNewArray(ColumnsArrayUtils.getNameableModelAsObjectArray(model),
                toInteger(model.annualized()),
                model.indicatorType(),
                model.numerator(),
                model.numeratorDescription(),
                model.denominator(),
                model.denominatorDescription(),
                model.url());
    }

    @Test
    public void have_extra_indicator_model_columns() {
        List<String> columnsList = Arrays.asList(columns);

        assertThat(columnsList.contains(Columns.ANNUALIZED)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.INDICATOR_TYPE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.NUMERATOR)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.NUMERATOR_DESCRIPTION)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.DENOMINATOR)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.DENOMINATOR_DESCRIPTION)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.URL)).isEqualTo(true);
    }
}