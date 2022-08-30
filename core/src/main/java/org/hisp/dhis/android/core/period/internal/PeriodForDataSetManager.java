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

package org.hisp.dhis.android.core.period.internal;

import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetCollectionRepository;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;

@Reusable
public class PeriodForDataSetManager {

    private final DataSetCollectionRepository dataSetCollectionRepository;
    private final ParentPeriodGenerator parentPeriodGenerator;
    private final PeriodStore periodStore;

    @Inject
    PeriodForDataSetManager(DataSetCollectionRepository dataSetCollectionRepository,
                            ParentPeriodGenerator parentPeriodGenerator,
                            PeriodStore periodStore) {
        this.dataSetCollectionRepository = dataSetCollectionRepository;
        this.parentPeriodGenerator = parentPeriodGenerator;
        this.periodStore = periodStore;
    }

    Single<List<Period>> getPeriodsForDataSet(String dataSetUid) {
        return dataSetCollectionRepository.uid(dataSetUid).get().map(dataSet -> {
            Integer dataSetFuturePeriods = dataSet.openFuturePeriods();
            int endPeriods = dataSetFuturePeriods == null ? 0 : dataSetFuturePeriods;

            List<Period> periods = parentPeriodGenerator.generatePeriods(dataSet.periodType(), endPeriods);
            storePeriods(periods);
            return periods;
        });
    }

    public List<Period> getPeriodsForDataSets(PeriodType periodType, List<DataSet> dataSets) {
        int maxFuturePeriods = 0;
        boolean someHasOpen = false;
        for (DataSet dataSet: dataSets) {
            if (dataSet.openFuturePeriods() != null) {
                maxFuturePeriods = Math.max(maxFuturePeriods, dataSet.openFuturePeriods());
                someHasOpen = true;
            }
        }

        if (!someHasOpen) {
            maxFuturePeriods = 1;
        }

        List<Period> periods = parentPeriodGenerator.generatePeriods(periodType, maxFuturePeriods);
        storePeriods(periods);
        return periods;
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
    public List<Period> getPeriodsInRange(PeriodType periodType, int startOffset, int endOffset) {
        List<Period> periods = parentPeriodGenerator.generatePeriods(periodType, startOffset, endOffset);
        storePeriods(periods);
        return periods;
    }

    private void storePeriods(List<Period> periods) {
        for (Period period : periods) {
            periodStore.updateOrInsertWhere(period);
        }
    }
}