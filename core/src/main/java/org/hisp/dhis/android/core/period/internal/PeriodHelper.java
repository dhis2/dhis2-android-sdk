/*
 * Copyright (c) 2004-2019, University of Oslo
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
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.period.internal;

import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;

@Singleton
public class PeriodHelper {

    private final PeriodStore periodStore;
    private final PeriodForDataSetManager periodForDataSetManager;
    private final ParentPeriodGenerator parentPeriodGenerator;
    private final PeriodParser periodParser;

    @Inject
    PeriodHelper(PeriodStore periodStore,
                 PeriodForDataSetManager periodForDataSetManager,
                 ParentPeriodGenerator parentPeriodGenerator,
                 PeriodParser periodParser) {
        this.periodStore = periodStore;
        this.periodForDataSetManager = periodForDataSetManager;
        this.parentPeriodGenerator = parentPeriodGenerator;
        this.periodParser = periodParser;
    }

    /**
     * Get a period object specifying a periodType and a date in the period.
     * If the period does not exist in the database, it is inserted.
     *
     * @param periodType Period type
     * @param date Date contained in the period
     * @return Period
     *
     * @deprecated Use {@link #getPeriodForPeriodTypeAndDate(PeriodType, Date)} instead.
     */
    @Deprecated
    public Period getPeriod(@NonNull PeriodType periodType, @NonNull Date date) {
        return blockingGetPeriodForPeriodTypeAndDate(periodType, date);
    }

    /**
     * Get a period object specifying a periodType and a date in the period.
     * If the period does not exist in the database, it is inserted.
     *
     * @param periodType Period type
     * @param date Date contained in the period
     * @return Period
     */
    public Period blockingGetPeriodForPeriodTypeAndDate(@NonNull PeriodType periodType, @NonNull Date date) {
        Period period = periodStore.selectPeriodByTypeAndDate(periodType, date);

        if (period == null) {
            Period newPeriod = parentPeriodGenerator.generatePeriod(periodType, date);
            periodStore.updateOrInsertWhere(newPeriod);
            return newPeriod;
        } else {
            return period;
        }
    }

    /**
     * Get a period object specifying a periodType and a date in the period.
     * If the period does not exist in the database, it is inserted.
     *
     * @param periodType Period type
     * @param date Date contained in the period
     *
     * @return {@code Single} with the generated period.
     * */
    public Single<Period> getPeriodForPeriodTypeAndDate(@NonNull PeriodType periodType, @NonNull Date date) {
        return Single.just(blockingGetPeriodForPeriodTypeAndDate(periodType, date));
    }

    /**
     * Get a period object specifying a periodId.
     * If the periodId does not exist in the database, it is inserted.
     *
     * @param periodId Period id.
     * @throws IllegalArgumentException if the periodId does not match any period
     * @return Period
     */
    public Period blockingGetPeriodForPeriodId(@NonNull String periodId) throws IllegalArgumentException {
        PeriodType periodType = PeriodType.periodTypeFromPeriodId(periodId);
        Date date = periodParser.parse(periodId, periodType);

        return blockingGetPeriodForPeriodTypeAndDate(periodType, date);
    }

    /**
     * Get a period object specifying a periodId.
     * If the periodId does not exist in the database, it is inserted.
     *
     * @param periodId Period id.
     * @throws IllegalArgumentException if the periodId does not match any period
     *
     * @return {@code Single} with the generated period.
     */
    public Single<Period> getPeriodForPeriodId(@NonNull String periodId) throws IllegalArgumentException {
        return Single.just(blockingGetPeriodForPeriodId(periodId));
    }

    public Single<List<Period>> getPeriodsForDataSet(String dataSetUid) {
        return periodForDataSetManager.getPeriodsForDataSet(dataSetUid);
    }

    public List<Period> blockingGetPeriodsForDataSet(String dataSetUid) {
        return getPeriodsForDataSet(dataSetUid).blockingGet();
    }
}