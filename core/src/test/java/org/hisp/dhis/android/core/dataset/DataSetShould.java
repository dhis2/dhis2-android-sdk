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
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.period.PeriodType;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class DataSetShould extends BaseObjectShould implements ObjectShould {

    public DataSetShould() {
        super("dataset/data_set.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        DataSet dataSet = objectMapper.readValue(jsonStream, DataSet.class);

        assertThat(dataSet.code()).isEqualTo("DS_394131");
        assertThat(dataSet.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.parseDate("2015-08-09T12:35:36.743"));
        assertThat(dataSet.uid()).isEqualTo("lyLU2wR22tC");
        assertThat(dataSet.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2012-06-10T00:36:10.036"));
        assertThat(dataSet.name()).isEqualTo("ART monthly summary");
        assertThat(dataSet.shortName()).isEqualTo("ART 2010");
        assertThat(dataSet.validCompleteOnly()).isEqualTo(false);
        assertThat(dataSet.dataElementDecoration()).isEqualTo(false);
        assertThat(dataSet.notifyCompletingUser()).isEqualTo(false);
        assertThat(dataSet.noValueRequiresComment()).isEqualTo(false);
        assertThat(dataSet.skipOffline()).isEqualTo(false);
        assertThat(dataSet.displayShortName()).isEqualTo("ART 2010");
        assertThat(dataSet.fieldCombinationRequired()).isEqualTo(false);
        assertThat(dataSet.renderHorizontally()).isEqualTo(false);
        assertThat(dataSet.renderAsTabs()).isEqualTo(false);
        assertThat(dataSet.mobile()).isEqualTo(false);
        assertThat(dataSet.version()).isEqualTo(22);
        assertThat(dataSet.timelyDays()).isEqualTo(0);
        assertThat(dataSet.periodType()).isEqualTo(PeriodType.Monthly);
        assertThat(dataSet.openFuturePeriods()).isEqualTo(0);
        assertThat(dataSet.expiryDays()).isEqualTo(0);
        assertThat(dataSet.categoryComboUid()).isEqualTo("O4VaNks6tta");
        assertThat(dataSet.indicators().get(0)).isEqualTo(ObjectWithUid.create("OEWO2PpiUKx"));
        assertThat(dataSet.access()).isEqualTo(Access.create(true, true, true, true,
                false, true, DataAccess.create(true, true)));
        assertThat(dataSet.style()).isEqualTo(ObjectStyle.create("#000", "my-icon-name"));
    }
}