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
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.PeriodType.Companion.periodTypeFromPeriodId
import org.koin.core.annotation.Singleton
import java.util.Date
import kotlin.math.ceil

@Singleton
class PeriodHelper internal constructor(
    private val periodStore: PeriodStore,
    private val periodForDataSetManager: PeriodForDataSetManager,
    private val parentPeriodGenerator: ParentPeriodGenerator,
    private val periodParser: PeriodParser,
) {
    /**
     * Get a period object specifying a periodType and a date in the period.
     * If the period does not exist in the database, it is inserted.
     *
     * @param periodType Period type
     * @param date       Date contained in the period
     * @return Period
     */
    @Deprecated(
        "Use {@link #getPeriodForPeriodTypeAndDate(PeriodType, Date)} instead.",
        ReplaceWith("getPeriodForPeriodTypeAndDate(periodType, date)"),
    )
    fun getPeriod(periodType: PeriodType, date: Date): Period {
        return blockingGetPeriodForPeriodTypeAndDate(periodType, date, 0)
    }

    /**
     * Get a period object specifying a periodType and a date in the period.
     * If the period does not exist in the database, it is inserted.
     *
     * @param periodType Period type
     * @param date       Date contained in the period
     * @return `Single` with the generated period.
     */
    fun getPeriodForPeriodTypeAndDate(periodType: PeriodType, date: Date): Single<Period> {
        return Single.just(blockingGetPeriodForPeriodTypeAndDate(periodType, date))
    }

    /**
     * Get a period object specifying a periodType and a date in the period.
     * If the period does not exist in the database, it is inserted.
     *
     * @param periodType   Period type
     * @param date         Date contained in the period
     * @param periodOffset Number of periods backwards or forwards relative to 'date'
     * @return `Single` with the generated period.
     */
    fun getPeriodForPeriodTypeAndDate(periodType: PeriodType, date: Date, periodOffset: Int): Single<Period> {
        return Single.just(blockingGetPeriodForPeriodTypeAndDate(periodType, date, periodOffset))
    }

    /**
     * Get a period object specifying a periodType and a date in the period.
     * If the period does not exist in the database, it is inserted.
     *
     * @param periodType   Period type
     * @param date         Date contained in the period
     * @param periodOffset Number of periods backwards or forwards relative to 'date'
     * @return Period
     */
    @JvmOverloads
    @Throws(IllegalStateException::class)
    fun blockingGetPeriodForPeriodTypeAndDate(periodType: PeriodType, date: Date, periodOffset: Int = 0): Period {
        return parentPeriodGenerator.generatePeriod(periodType, date, periodOffset)?.also { period ->
            periodStore.selectByPeriodId(period.periodId()) ?: periodStore.updateOrInsertWhere(period)
        } ?: error("Generated period is null")
    }

    /**
     * Get a period object specifying a periodId.
     * If the periodId does not exist in the database, it is inserted.
     *
     * @param periodId Period id.
     * @return Period
     * @throws IllegalArgumentException if the periodId does not match any period
     */
    @Throws(IllegalArgumentException::class)
    fun blockingGetPeriodForPeriodId(periodId: String): Period {
        val periodType = periodTypeFromPeriodId(periodId)
        val date = Date(periodParser.parse(periodId, periodType).toEpochMilliseconds())

        return blockingGetPeriodForPeriodTypeAndDate(periodType, date)
    }

    /**
     * Get a period object specifying a periodId.
     * If the periodId does not exist in the database, it is inserted.
     *
     * @param periodId Period id.
     * @return `Single` with the generated period.
     * @throws IllegalArgumentException if the periodId does not match any period
     */
    @Throws(IllegalArgumentException::class)
    fun getPeriodForPeriodId(periodId: String): Single<Period> {
        return Single.just(blockingGetPeriodForPeriodId(periodId))
    }

    fun getPeriodsForDataSet(dataSetUid: String): Single<List<Period>> {
        return periodForDataSetManager.getPeriodsForDataSet(dataSetUid)
    }

    fun blockingGetPeriodsForDataSet(dataSetUid: String): List<Period> {
        return getPeriodsForDataSet(dataSetUid).blockingGet()
    }

    companion object {
        private const val MILLIS_IN_A_DAY = 86400000

        fun getDays(period: Period): Int {
            val diffInMillis = period.endDate()!!.time - period.startDate()!!.time
            return ceil(diffInMillis / MILLIS_IN_A_DAY.toDouble()).toInt()
        }
    }
}
