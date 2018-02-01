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

package org.hisp.dhis.android.core.datavalue;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ModelAbstractShould;
import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DELETED;

@RunWith(AndroidJUnit4.class)
public class DataValueModelShould extends ModelAbstractShould<DataValueModel, DataValue> {
    private static final long ID = 2L;
    private static final String DATA_ELEMENT = "dataElement";
    private static final String PERIOD = "period";
    private static final String ORGANISATION_UNIT = "organisationUnit";
    private static final String CATEGORY_OPTION_COMBO = "categoryOptionCombo";
    private static final String ATTRIBUTE_OPTION_COMBO = "attributeOptionCombo";
    private static final String VALUE = "value";
    private static final String STORED_BY = "storedBy";
    private static final String COMMENT = "comment";
    private static final boolean FOLLOW_UP = false;

    private final Date date;
    private final String dateString;

    public DataValueModelShould() {
        super(DataValueModel.Columns.all(), 11, DataValueModel.factory);
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Override
    protected DataValue buildPojo() {
        return DataValue.create(DATA_ELEMENT, PERIOD, ORGANISATION_UNIT, CATEGORY_OPTION_COMBO,
                ATTRIBUTE_OPTION_COMBO, VALUE, STORED_BY, date, COMMENT, FOLLOW_UP, DELETED);
    }

    @Override
    protected DataValueModel buildModel() {
        DataValueModel.Builder dataValueModelBuilder = DataValueModel.builder();
        dataValueModelBuilder
                .id(ID)
                .dataElement(DATA_ELEMENT)
                .period(PERIOD)
                .organisationUnit(ORGANISATION_UNIT)
                .categoryOptionCombo(CATEGORY_OPTION_COMBO)
                .attributeOptionCombo(ATTRIBUTE_OPTION_COMBO)
                .value(VALUE)
                .storedBy(STORED_BY)
                .lastUpdated(date)
                .comment(COMMENT)
                .followUp(FOLLOW_UP);
        return dataValueModelBuilder.build();
    }

    @Override
    protected Object[] getModelAsObjectArray() {
        return Utils.appendInNewArray(ColumnsArrayUtils.getModelAsObjectArray(model),
                model.dataElement(), model.period(), model.organisationUnit(),
                model.categoryOptionCombo(), model.attributeOptionCombo(), model.value(),
                model.storedBy(), model.lastUpdated(), model.comment(), toInteger(model.followUp()));
    }
}
