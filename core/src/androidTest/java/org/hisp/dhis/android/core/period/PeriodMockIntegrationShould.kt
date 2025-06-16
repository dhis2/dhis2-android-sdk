/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.period

import com.google.common.truth.Truth
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.period.internal.PeriodStore
import org.hisp.dhis.android.core.period.internal.PeriodStoreImpl
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test
import java.text.ParseException

class PeriodMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    @Throws(ParseException::class)
    fun get_period_passing_period_type_and_a_date() {
        val period = d2.periodModule().periodHelper().blockingGetPeriodForPeriodTypeAndDate(
            PeriodType.BiWeekly,
            BaseIdentifiableObject.DATE_FORMAT.parse("2019-07-08T12:24:25.319"),
        )
        Truth.assertThat(period.periodId()).isEqualTo("2019BiW14")
    }

    @Test
    @Throws(ParseException::class)
    fun create_period_in_database_if_not_exist() = runTest {
        val periodStore: PeriodStore = PeriodStoreImpl(databaseAdapter)

        val date = BaseIdentifiableObject.DATE_FORMAT.parse("2010-12-24T12:24:25.319")

        var periodInDb = periodStore.selectPeriodByTypeAndDate(PeriodType.Monthly, date)
        Truth.assertThat(periodInDb).isNull()

        val period = d2.periodModule().periodHelper().blockingGetPeriodForPeriodTypeAndDate(
            PeriodType.Monthly,
            date,
        )
        Truth.assertThat(period).isNotNull()
        Truth.assertThat(period.periodId()).isEqualTo("201012")

        periodInDb = periodStore.selectPeriodByTypeAndDate(PeriodType.Monthly, date)
        Truth.assertThat(periodInDb).isNotNull()

        periodStore.deleteWhere(period)
    }

    @Test
    fun get_periods() {
        Truth.assertThat(d2.periodModule().periods().blockingCount()).isEqualTo(230)
    }

    @Test
    fun ensure_future_periods_are_downloaded() {
        Truth.assertThat(d2.periodModule().periods().byPeriodType().eq(PeriodType.Monthly).blockingCount())
            .isEqualTo(MONTHLY_PERIODS + 4)
    }

    companion object {
        private const val MONTHLY_PERIODS = 11 // Copy from ParentPeriodGeneratorImpl to keep it private
    }
}
