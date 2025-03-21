/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.testapp.dataset

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class DataSetInstanceSummaryCollectionRepositoryMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {

    @Test
    fun find_all() {
        val summaries = d2.dataSetModule().dataSetInstanceSummaries()
            .blockingGet()

        assertThat(summaries.size).isEqualTo(3)
        summaries
            .filter { it.dataSetUid() == "lyLU2wR22tC" }
            .forEach {
                assertThat(it.dataSetInstanceCount()).isEqualTo(2)
                assertThat(it.valueCount()).isEqualTo(2)
            }
    }

    @Test
    fun filter_by_dataset() {
        val summaries = d2.dataSetModule().dataSetInstanceSummaries()
            .byDataSetUid().eq("lyLU2wR22tC")
            .blockingGet()

        assertThat(summaries.size).isEqualTo(3)
        summaries
            .filter { it.dataSetUid() == "lyLU2wR22tC" }
            .forEach {
                assertThat(it.dataSetInstanceCount()).isEqualTo(2)
                assertThat(it.valueCount()).isEqualTo(2)
            }
    }

    @Test
    fun filter_by_period() {
        val summaries = d2.dataSetModule().dataSetInstanceSummaries()
            .byPeriod().eq("2018")
            .blockingGet()

        assertThat(summaries.size).isEqualTo(3)
        summaries
            .filter { it.dataSetUid() == "lyLU2wR22tC" }
            .forEach {
                assertThat(it.dataSetInstanceCount()).isEqualTo(0)
                assertThat(it.valueCount()).isEqualTo(0)
            }
    }

    @Test
    fun filter_by_period_type() {
        val summaries = d2.dataSetModule().dataSetInstanceSummaries()
            .byPeriodType().eq(PeriodType.Yearly)
            .blockingGet()

        assertThat(summaries.size).isEqualTo(3)
    }

    @Test
    fun filter_by_period_start_date() {
        val summaries = d2.dataSetModule().dataSetInstanceSummaries()
            .byPeriodStartDate().after(BaseIdentifiableObject.parseDate("2018-07-15T00:00:00.000"))
            .blockingGet()

        assertThat(summaries.size).isEqualTo(3)
    }

    @Test
    fun filter_by_period_end_date() {
        val summaries = d2.dataSetModule().dataSetInstanceSummaries()
            .byPeriodEndDate().after(BaseIdentifiableObject.parseDate("2018-07-15T00:00:00.000"))
            .blockingGet()

        assertThat(summaries.size).isEqualTo(3)
    }

    @Test
    fun filter_by_organisation_unit() {
        val summaries = d2.dataSetModule().dataSetInstanceSummaries()
            .byOrganisationUnitUid().eq("DiszpKrYNg8")
            .blockingGet()

        assertThat(summaries.size).isEqualTo(3)
        summaries
            .filter { it.dataSetUid() == "lyLU2wR22tC" }
            .forEach {
                assertThat(it.dataSetInstanceCount()).isEqualTo(1)
                assertThat(it.valueCount()).isEqualTo(1)
            }
    }
}
