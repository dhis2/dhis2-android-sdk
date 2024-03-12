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
package org.hisp.dhis.android.core.period.internal

import io.reactivex.Single
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetCollectionRepository
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.koin.core.annotation.Singleton

@Singleton
internal class PeriodForDataSetManager(
    private val dataSetCollectionRepository: DataSetCollectionRepository,
    private val parentPeriodGenerator: ParentPeriodGenerator,
    private val periodStore: PeriodStore,
) {
    fun getPeriodsForDataSet(dataSetUid: String?): Single<List<Period>> {
        return dataSetCollectionRepository.uid(dataSetUid).get().map { dataSet: DataSet ->
            val dataSetFuturePeriods = dataSet.openFuturePeriods()
            val endPeriods = dataSetFuturePeriods ?: 0
            val periods = parentPeriodGenerator.generatePeriods(
                dataSet.periodType()!!,
                endPeriods,
            )
            storePeriods(periods)
            periods
        }
    }

    fun getPeriodsForDataSets(periodType: PeriodType, dataSets: List<DataSet>): List<Period> {
        var maxFuturePeriods = 0
        var someHasOpen = false
        for (dataSet in dataSets) {
            if (dataSet.openFuturePeriods() != null) {
                maxFuturePeriods = maxFuturePeriods.coerceAtLeast(dataSet.openFuturePeriods()!!)
                someHasOpen = true
            }
        }
        if (!someHasOpen) {
            maxFuturePeriods = 1
        }
        val periods = parentPeriodGenerator.generatePeriods(
            periodType,
            maxFuturePeriods,
        )
        storePeriods(periods)
        return periods
    }

    /**
     * Generate a list of periods given a PeriodType and a range. For example, the method call
     * getPeriodsInRange(PeriodType.Monthly, -5, 1) will generate the last 5 months and the next month.
     *
     * @param periodType Period type
     * @param startOffset Relative period offset, it could be positive or negative
     * @param endOffset Relative period offset, it could be positive or negative
     * @return List of periods in range
     */
    fun getPeriodsInRange(periodType: PeriodType, startOffset: Int, endOffset: Int): List<Period> {
        val periods = parentPeriodGenerator.generatePeriods(
            periodType,
            startOffset,
            endOffset,
        )
        storePeriods(periods)
        return periods
    }

    private fun storePeriods(periods: List<Period>) {
        for (period in periods) {
            periodStore.updateOrInsertWhere(period)
        }
    }
}
