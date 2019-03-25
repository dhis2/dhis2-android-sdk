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

package org.hisp.dhis.android.core.dataset;

import android.database.Cursor;
import androidx.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.IdentifiableModelAbstractShould;
import org.hisp.dhis.android.core.dataset.SectionModel.Columns;
import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillIdentifiableModelProperties;

@RunWith(AndroidJUnit4.class)
public class SectionModelShould extends IdentifiableModelAbstractShould<SectionModel> {

    public SectionModelShould() {

        super(new Columns().all(), 11);
    }

    @Override
    protected SectionModel buildModel() {

        SectionModel.Builder sectionModelBuilder = SectionModel.builder();
        fillIdentifiableModelProperties(sectionModelBuilder);

        sectionModelBuilder.description("description")
                .sortOrder(2)
                .dataSet("dataset_uid")
                .showRowTotals(false)
                .showColumnTotals(false);

        return sectionModelBuilder.build();
    }

    @Override
    protected SectionModel cursorToModel(Cursor cursor) {

        return SectionModel.create(cursor);
    }

    @Override
    protected Object[] getModelAsObjectArray() {

        return Utils.appendInNewArray(ColumnsArrayUtils.getIdentifiableModelAsObjectArray(model),
                model.description(),
                model.sortOrder(),
                model.dataSet(),
                toInteger(model.showRowTotals()),
                toInteger(model.showColumnTotals()));
    }

    @Test
    public void have_extra_data_set_model_columns() {

        List<String> columnsList = Arrays.asList(columns);

        assertThat(columnsList.contains(Columns.DESCRIPTION)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.SORT_ORDER)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.DATA_SET)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.SHOW_ROW_TOTALS)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.SHOW_COLUMN_TOTALS)).isEqualTo(true);
    }
}