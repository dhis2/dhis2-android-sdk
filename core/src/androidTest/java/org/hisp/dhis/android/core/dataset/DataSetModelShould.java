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

package org.hisp.dhis.android.core.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.common.NameableModelAbstractShould;
import org.hisp.dhis.android.core.common.PeriodType;
import org.hisp.dhis.android.core.dataset.DataSetModel.Columns;
import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.CODE;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DELETED;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DESCRIPTION;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DISPLAY_DESCRIPTION;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DISPLAY_NAME;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DISPLAY_SHORT_NAME;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.LAST_UPDATED;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.NAME;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.SHORT_NAME;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.UID;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.fillNameableModelProperties;

@RunWith(AndroidJUnit4.class)
public class DataSetModelShould extends NameableModelAbstractShould<DataSetModel, DataSet> {

    public DataSetModelShould() {
        super(DataSetModel.Columns.all(), 25, DataSetModel.Factory);
    }

    @Override
    protected DataSetModel buildModel() {
        DataSetModel.Builder dataSetModelBuilder = DataSetModel.builder();
        fillNameableModelProperties(dataSetModelBuilder);
        dataSetModelBuilder
                .periodType(PeriodType.Monthly)
                .categoryCombo("cc_uid")
                .mobile(false)
                .version(1)
                .expiryDays(10)
                .timelyDays(100)
                .notifyCompletingUser(false)
                .openFuturePeriods(0)
                .fieldCombinationRequired(false)
                .validCompleteOnly(false)
                .noValueRequiresComment(false)
                .skipOffline(false)
                .dataElementDecoration(false)
                .renderAsTabs(false)
                .renderHorizontally(false);
        return dataSetModelBuilder.build();
    }

    @Override
    protected DataSet buildPojo() {
        return DataSet.create(UID, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED, SHORT_NAME,
                DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, PeriodType.Monthly,
                CategoryCombo.create("cc_uid", CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED,
                        new ArrayList<Category>(), new ArrayList<CategoryOptionCombo>(), DELETED),
                false, 1, 10, 100, false,
                0, false, false,
                false, false, false,
                false, false, new ArrayList<DataElementCategoryCombo>(), DELETED);
    }

    @Override
    protected Object[] getModelAsObjectArray() {
        return Utils.appendInNewArray(ColumnsArrayUtils.getNameableModelAsObjectArray(model),
                model.periodType(), model.categoryCombo(), toInteger(model.mobile()), model.version(),
                model.expiryDays(), model.timelyDays(), toInteger(model.notifyCompletingUser()),
                model.openFuturePeriods(), toInteger(model.fieldCombinationRequired()),
                toInteger(model.validCompleteOnly()), toInteger(model.noValueRequiresComment()),
                toInteger(model.skipOffline()), toInteger(model.dataElementDecoration()),
                toInteger(model.renderAsTabs()), toInteger(model.renderHorizontally()));
    }

    @Test
    public void have_extra_data_set_model_columns() {
        List<String> columnsList = Arrays.asList(columns);

        assertThat(columnsList.contains(Columns.PERIOD_TYPE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.CATEGORY_COMBO)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.MOBILE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.VERSION)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.EXPIRY_DAYS)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.TIMELY_DAYS)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.NOTIFY_COMPLETING_USER)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.OPEN_FUTURE_PERIODS)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.FIELD_COMBINATION_REQUIRED)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.VALID_COMPLETE_ONLY)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.NO_VALUE_REQUIRES_COMMENT)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.SKIP_OFFLINE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.DATA_ELEMENT_DECORATION)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.RENDER_AS_TABS)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.RENDER_HORIZONTALLY)).isEqualTo(true);
    }
}