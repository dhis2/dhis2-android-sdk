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

import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.common.NameableModelBuilderAbstractShould;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.period.PeriodType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CODE;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.COLOR;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DELETED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DESCRIPTION;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DISPLAY_DESCRIPTION;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DISPLAY_NAME;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DISPLAY_SHORT_NAME;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.ICON;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.LAST_UPDATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.NAME;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.SHORT_NAME;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.UID;

@RunWith(JUnit4.class)
public class DataSetModelBuilderShould extends NameableModelBuilderAbstractShould<DataSet, DataSetModel> {

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
    }

    @Override
    protected DataSet buildPojo() {
        return DataSet.create(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                CREATED,
                LAST_UPDATED,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                PeriodType.Monthly,
                ObjectWithUid.create("cc_uid"),
                false,
                1,
                10,
                100,
                false,
                0,
                true,
                true,
                false,
                true,
                false,
                true,
                false,
                new ArrayList<DataElementUids>(),
                new ArrayList<ObjectWithUid>(),
                new ArrayList<Section>(),
                Access.create(true, true, false, true,
                        true, true, DataAccess.create(true, false)),
                ObjectStyle.create(COLOR, ICON),
                DELETED);
    }

    @Override
    protected ModelBuilder<DataSet, DataSetModel> modelBuilder() {
        return new DataSetModelBuilder();
    }

    @Test
    public void copy_pojo_dataset_properties() {
        assertThat(model.mobile()).isEqualTo(pojo.mobile());
        assertThat(model.version()).isEqualTo(pojo.version());
        assertThat(model.expiryDays()).isEqualTo(pojo.expiryDays());
        assertThat(model.timelyDays()).isEqualTo(pojo.timelyDays());
        assertThat(model.notifyCompletingUser()).isEqualTo(pojo.notifyCompletingUser());
        assertThat(model.openFuturePeriods()).isEqualTo(pojo.openFuturePeriods());
        assertThat(model.fieldCombinationRequired()).isEqualTo(pojo.fieldCombinationRequired());
        assertThat(model.validCompleteOnly()).isEqualTo(pojo.validCompleteOnly());
        assertThat(model.noValueRequiresComment()).isEqualTo(pojo.noValueRequiresComment());
        assertThat(model.skipOffline()).isEqualTo(pojo.skipOffline());
        assertThat(model.dataElementDecoration()).isEqualTo(pojo.dataElementDecoration());
        assertThat(model.renderAsTabs()).isEqualTo(pojo.renderAsTabs());
        assertThat(model.renderHorizontally()).isEqualTo(pojo.renderHorizontally());
        assertThat(model.accessDataWrite()).isEqualTo(pojo.access().data().write());
    }
}
