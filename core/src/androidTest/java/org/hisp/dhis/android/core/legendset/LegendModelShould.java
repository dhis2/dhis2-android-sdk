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

package org.hisp.dhis.android.core.legendset;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.IdentifiableModelAbstractShould;
import org.hisp.dhis.android.core.legendset.LegendModel.Columns;
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
public class LegendModelShould extends IdentifiableModelAbstractShould<Legend, LegendModel> {

    private final Double startValue = 20.0;
    private final Double endValue = 30.5;
    private final String color = "#FFFFFF";
    private final String legendSetUid = "test_legend_set_uid";

    public LegendModelShould() {
            super(Columns.all(), 10, new LegendModelBuilder(LegendSet.create(
                    "test_legend_set_uid", null, null, null,
                    null, null, null, null, null)));
    }

    @Override
    protected LegendModel buildModel() {
        LegendModel.Builder builder = LegendModel.builder();
        fillIdentifiableModelProperties(builder);
        builder
                .startValue(startValue)
                .endValue(endValue)
                .color(color)
                .legendSet(legendSetUid);
        return builder.build();
    }

    @Override
    protected LegendModel cursorToModel(Cursor cursor) {
        return LegendModel.create(cursor);
    }

    @Override
    protected Legend buildPojo() {
        return Legend.create(UID, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED, DELETED,
                startValue, endValue, color);
    }

    @Override
    protected Object[] getModelAsObjectArray() {
        return Utils.appendInNewArray(ColumnsArrayUtils.getIdentifiableModelAsObjectArray(model),
                model.startValue(), model.endValue(), model.color(), model.legendSet());
    }

    @Test
    public void have_extra_legend_model_columns() {
        List<String> columnsList = Arrays.asList(columns);

        assertThat(columnsList.contains(Columns.START_VALUE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.END_VALUE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.COLOR)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.LEGEND_SET)).isEqualTo(true);
    }
}