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

package org.hisp.dhis.android.core.domain.aggregated.data.internal;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.Date;

@AutoValue
public abstract class AggregatedDataSync implements CoreObject {

    public static Builder builder() {
        return new AutoValue_AggregatedDataSync.Builder();
    }

    @NonNull
    public abstract String dataSet();

    @NonNull
    public abstract PeriodType periodType();

    @NonNull
    public abstract Integer pastPeriods();

    @NonNull
    public abstract Integer futurePeriods();

    @NonNull
    public abstract Integer dataElementsHash();

    @NonNull
    public abstract Integer organisationUnitsHash();

    @NonNull
    public abstract Date lastUpdated();

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder dataSet(String dataSet);

        public abstract Builder periodType(PeriodType periodType);

        public abstract Builder pastPeriods(Integer pastPeriods);

        public abstract Builder futurePeriods(Integer futurePeriods);

        public abstract Builder dataElementsHash(Integer dataElementsHash);

        public abstract Builder organisationUnitsHash(Integer organisationUnitHash);

        public abstract Builder lastUpdated(Date lastUpdated);

        public abstract AggregatedDataSync build();
    }
}
