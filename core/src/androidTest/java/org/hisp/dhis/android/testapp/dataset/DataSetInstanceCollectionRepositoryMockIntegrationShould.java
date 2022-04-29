/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.testapp.dataset;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.dataset.DataSetInstance;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class DataSetInstanceCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<DataSetInstance> dataSetInstances = d2.dataSetModule().dataSetInstances()
                .blockingGet();
        assertThat(dataSetInstances.size()).isEqualTo(4);
    }

    @Test
    public void filter_by_dataset() {
        List<DataSetInstance> dataSetInstances = d2.dataSetModule().dataSetInstances()
                .byDataSetUid().eq("lyLU2wR22tC")
                .blockingGet();
        assertThat(dataSetInstances.size()).isEqualTo(4);
    }

    @Test
    public void filter_by_period() {
        List<DataSetInstance> dataSetInstances = d2.dataSetModule().dataSetInstances()
                .byPeriod().eq("2018")
                .blockingGet();
        assertThat(dataSetInstances.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_period_type() {
        List<DataSetInstance> dataSetInstances = d2.dataSetModule().dataSetInstances()
                .byPeriodType().eq(PeriodType.Yearly)
                .blockingGet();
        assertThat(dataSetInstances.size()).isEqualTo(3);
    }

    @Test
    public void filter_by_period_start_date() throws ParseException {
        List<DataSetInstance> dataSetInstances = d2.dataSetModule().dataSetInstances()
                .byPeriodStartDate().after(BaseIdentifiableObject.parseDate("2018-07-15T00:00:00.000"))
                .blockingGet();
        assertThat(dataSetInstances.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_period_end_date() throws ParseException {
        List<DataSetInstance> dataSetInstances = d2.dataSetModule().dataSetInstances()
                .byPeriodEndDate().after(BaseIdentifiableObject.parseDate("2018-07-15T00:00:00.000"))
                .blockingGet();
        assertThat(dataSetInstances.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_organisation_unit() {
        List<DataSetInstance> dataSetInstances = d2.dataSetModule().dataSetInstances()
                .byOrganisationUnitUid().eq("DiszpKrYNg8")
                .blockingGet();
        assertThat(dataSetInstances.size()).isEqualTo(4);
    }

    @Test
    public void fill_completion_information() {
        List<DataSetInstance> dataSetInstanceCompleted = d2.dataSetModule().dataSetInstances()
                .byPeriod().eq("2018")
                .blockingGet();
        assertThat(dataSetInstanceCompleted.get(0).completed()).isTrue();
        assertThat(dataSetInstanceCompleted.get(0).completionDate()).isNotNull();

        List<DataSetInstance> dataSetInstanceUncompleted = d2.dataSetModule().dataSetInstances()
                .byPeriod().eq("201907")
                .blockingGet();
        assertThat(dataSetInstanceUncompleted.get(0).completed()).isFalse();
        assertThat(dataSetInstanceUncompleted.get(0).completionDate()).isNull();
    }

    @Test
    public void does_not_include_values_with_other_coc() {
        List<DataSetInstance> dataSetInstances = d2.dataSetModule().dataSetInstances()
                .byPeriod().eq("201907")
                .blockingGet();
        assertThat(dataSetInstances.size()).isEqualTo(1);
        assertThat(dataSetInstances.get(0).valueCount()).isEqualTo(1);
    }

}