/*
 * Copyright (c) 2004-2018, University of Oslo
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
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.core.datavalue;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.ModelAbstractShould;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED_STR;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.LAST_UPDATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.LAST_UPDATED_STR;

@RunWith(AndroidJUnit4.class)
public class DataValueModelShould extends ModelAbstractShould<DataValueModel> {
    private static final String DATA_ELEMENT = "dataElement";
    private static final String PERIOD = "period";
    private static final String ORGANISATION_UNIT = "organisationUnit";
    private static final String CATEGORY_OPTION_COMBO = "categoryOptionCombo";
    private static final String ATTRIBUTE_OPTION_COMBO = "attributeOptionCombo";
    private static final String VALUE = "value";
    private static final String STORED_BY = "storedBy";
    private static final String COMMENT = "comment";
    private static final boolean FOLLOW_UP = false;
    private static final State STATE = State.SYNCED;

    public DataValueModelShould() {
        super(new DataValueModel.Columns().all(), 12);
    }

    @Override
    protected DataValueModel cursorToModel(Cursor cursor) {
        return DataValueModel.create(cursor);
    }

    @Override
    protected DataValueModel buildModel() {
        DataValueModel.Builder dataValueModelBuilder = DataValueModel.builder();
        dataValueModelBuilder
                .dataElement(DATA_ELEMENT)
                .period(PERIOD)
                .organisationUnit(ORGANISATION_UNIT)
                .categoryOptionCombo(CATEGORY_OPTION_COMBO)
                .attributeOptionCombo(ATTRIBUTE_OPTION_COMBO)
                .value(VALUE)
                .storedBy(STORED_BY)
                .created(CREATED)
                .lastUpdated(LAST_UPDATED)
                .comment(COMMENT)
                .followUp(FOLLOW_UP)
                .state(STATE);
        return dataValueModelBuilder.build();
    }

    @Override
    protected Object[] getModelAsObjectArray() {
        return Utils.appendInNewArray(ColumnsArrayUtils.getModelAsObjectArray(model),
                model.dataElement(),
                model.period(),
                model.organisationUnit(),
                model.categoryOptionCombo(),
                model.attributeOptionCombo(),
                model.value(),
                model.storedBy(),
                CREATED_STR,
                LAST_UPDATED_STR,
                model.comment(),
                toInteger(model.followUp()),
                model.state());
    }

    @Test
    public void have_data_value_columns() {
        List<String> columnsList = Arrays.asList(new DataValueModel.Columns().all());

        assertThat(columnsList.contains(DataValueModel.Columns.DATA_ELEMENT)).isEqualTo(true);
        assertThat(columnsList.contains(DataValueModel.Columns.PERIOD)).isEqualTo(true);
        assertThat(columnsList.contains(DataValueModel.Columns.ORGANISATION_UNIT)).isEqualTo(true);
        assertThat(columnsList.contains(DataValueModel.Columns.CATEGORY_OPTION_COMBO)).isEqualTo(true);
        assertThat(columnsList.contains(DataValueModel.Columns.ATTRIBUTE_OPTION_COMBO)).isEqualTo(true);
        assertThat(columnsList.contains(DataValueModel.Columns.VALUE)).isEqualTo(true);
        assertThat(columnsList.contains(DataValueModel.Columns.STORED_BY)).isEqualTo(true);
        assertThat(columnsList.contains(DataValueModel.Columns.CREATED)).isEqualTo(true);
        assertThat(columnsList.contains(DataValueModel.Columns.LAST_UPDATED)).isEqualTo(true);
        assertThat(columnsList.contains(DataValueModel.Columns.COMMENT)).isEqualTo(true);
        assertThat(columnsList.contains(DataValueModel.Columns.FOLLOW_UP)).isEqualTo(true);
        assertThat(columnsList.contains(DataValueModel.Columns.STATE)).isEqualTo(true);


    }
}
