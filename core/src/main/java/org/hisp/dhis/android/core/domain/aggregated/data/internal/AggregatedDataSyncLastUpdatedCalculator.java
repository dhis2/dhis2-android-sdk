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

package org.hisp.dhis.android.core.domain.aggregated.data.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.dataset.DataSet;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
class AggregatedDataSyncLastUpdatedCalculator {

    private final AggregatedDataSyncHashHelper hashHelper;

    @Inject
    AggregatedDataSyncLastUpdatedCalculator(AggregatedDataSyncHashHelper hashHelper) {
        this.hashHelper = hashHelper;
    }

    Date getLastUpdated(@Nullable AggregatedDataSync syncValue, @NonNull DataSet dataSet, @NonNull Integer pastPeriods,
                        @NonNull Integer futurePeriods, @NonNull Integer organisationUnitHash) {
        if (syncValue == null ||
                syncValue.periodType() != dataSet.periodType() ||
                syncValue.futurePeriods() < futurePeriods ||
                syncValue.pastPeriods() < pastPeriods ||
                syncValue.dataElementsHash() != hashHelper.getDataSetDataElementsHash(dataSet) ||
                syncValue.organisationUnitsHash().intValue() != organisationUnitHash) {
            return null;
        } else {
            return getDateMinus24Hours(syncValue.lastUpdated());
        }
    }

    private Date getDateMinus24Hours(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
}