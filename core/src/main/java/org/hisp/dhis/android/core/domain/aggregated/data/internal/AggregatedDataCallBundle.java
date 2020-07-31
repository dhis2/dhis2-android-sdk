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

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.dataset.DataSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@AutoValue
public abstract class AggregatedDataCallBundle {
    public abstract AggregatedDataCallBundleKey key();

    public abstract List<DataSet> dataSets();

    public abstract Collection<String> periodIds();

    public abstract Collection<String> rootOrganisationUnitUids();

    public abstract Set<String> allOrganisationUnitUidsSet();

    public static Builder builder() {
        return new AutoValue_AggregatedDataCallBundle.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder key(AggregatedDataCallBundleKey key);

        public abstract Builder dataSets(List<DataSet> dataSets);

        public abstract Builder periodIds(Collection<String> periodIds);

        public abstract Builder rootOrganisationUnitUids(Collection<String> orgUnitUids);

        public abstract Builder allOrganisationUnitUidsSet(Set<String> allOrganisationUnitUidsSet);

        public abstract AggregatedDataCallBundle build();
    }
}
