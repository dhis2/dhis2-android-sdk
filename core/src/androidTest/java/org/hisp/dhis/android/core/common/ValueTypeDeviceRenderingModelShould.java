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

package org.hisp.dhis.android.core.common;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.ValueTypeDeviceRenderingModel.Columns;
import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DEVICE_TYPE;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.TABLE;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.UID;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.VALUE_TYPE_RENDERING_TYPE;

@RunWith(AndroidJUnit4.class)
public class ValueTypeDeviceRenderingModelShould extends LinkModelAbstractShould<ValueTypeDeviceRenderingModel> {

    public ValueTypeDeviceRenderingModelShould() {
        super(new Columns().all(), 8);
    }

    @Override
    protected ValueTypeDeviceRenderingModel buildModel() {
        ValueTypeDeviceRenderingModel.Builder valueTypeDeviceRenderingModelBuilder =
                ValueTypeDeviceRenderingModel.builder();
        valueTypeDeviceRenderingModelBuilder
                .uid(UID)
                .objectTable(TABLE)
                .deviceType(DEVICE_TYPE)
                .type(VALUE_TYPE_RENDERING_TYPE)
                .min(0)
                .max(10)
                .step(1)
                .decimalPoints(0);
        return valueTypeDeviceRenderingModelBuilder.build();
    }

    @Override
    protected ValueTypeDeviceRenderingModel cursorToModel(Cursor cursor) {
        return ValueTypeDeviceRenderingModel.create(cursor);
    }

    @Override
    protected Object[] getModelAsObjectArray() {
        return Utils.appendInNewArray(ColumnsArrayUtils.getModelAsObjectArray(model),
                model.uid(), model.objectTable(), model.deviceType(), model.type(), model.min(), model.max(),
                model.step(), model.decimalPoints());
    }

    @Test
    public void create_model_from_pojo() {
        assertThat(ValueTypeDeviceRenderingModel.builder().uid(UID).objectTable(TABLE).deviceType(DEVICE_TYPE)
                .type(VALUE_TYPE_RENDERING_TYPE).min(0).max(10).step(1).decimalPoints(0).build())
                .isEqualTo(model);
    }

    @Test
    public void have_value_type_device_rendering_model_columns() {
        List<String> columnsList = Arrays.asList(columns);

        assertThat(columnsList.contains(Columns.UID)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.OBJECT_TABLE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.DEVICE_TYPE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.TYPE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.MAX)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.MIN)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.STEP)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.DECIMAL_POINTS)).isEqualTo(true);
    }
}