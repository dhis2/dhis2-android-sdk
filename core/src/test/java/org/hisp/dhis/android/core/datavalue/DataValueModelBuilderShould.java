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

import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.common.ModelBuilderAbstractShould;
import org.hisp.dhis.android.core.common.State;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DELETED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.LAST_UPDATED;

@RunWith(JUnit4.class)
public class DataValueModelBuilderShould extends ModelBuilderAbstractShould<DataValue,
        DataValueModel> {

    private static final String DATA_ELEMENT = "dataElement";
    private static final String PERIOD = "period";
    private static final String ORGANISATION_UNIT = "organisationUnit";
    private static final String CATEGORY_OPTION_COMBO = "categoryOptionCombo";
    private static final String ATTRIBUTE_OPTION_COMBO = "attributeOptionCombo";
    private static final String VALUE = "value";
    private static final String STORED_BY = "storedBy";
    private static final String COMMENT = "comment";
    private static final boolean FOLLOW_UP = false;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
    }

    @Override
    protected DataValue buildPojo() {
        return DataValue.create(
                DATA_ELEMENT,
                PERIOD,
                ORGANISATION_UNIT,
                CATEGORY_OPTION_COMBO,
                ATTRIBUTE_OPTION_COMBO,
                VALUE,
                STORED_BY,
                CREATED,
                LAST_UPDATED,
                COMMENT,
                FOLLOW_UP,
                DELETED);
    }

    @Override
    protected ModelBuilder<DataValue, DataValueModel> modelBuilder() {
        return new DataValueModelBuilder(State.SYNCED);
    }

    @Test
    public void copy_pojo_data_value_properties() {
        assertThat(model.dataElement()).isEqualTo(pojo.dataElement());
        assertThat(model.period()).isEqualTo(pojo.period());
        assertThat(model.organisationUnit()).isEqualTo(pojo.organisationUnit());
        assertThat(model.categoryOptionCombo()).isEqualTo(pojo.categoryOptionCombo());
        assertThat(model.attributeOptionCombo()).isEqualTo(pojo.attributeOptionCombo());
        assertThat(model.value()).isEqualTo(pojo.value());
        assertThat(model.storedBy()).isEqualTo(pojo.storedBy());
        assertThat(model.created()).isEqualTo(pojo.created());
        assertThat(model.lastUpdated()).isEqualTo(pojo.lastUpdated());
        assertThat(model.comment()).isEqualTo(pojo.comment());
        assertThat(model.followUp()).isEqualTo(pojo.followUp());
    }
}
