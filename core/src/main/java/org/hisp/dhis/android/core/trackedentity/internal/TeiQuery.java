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

package org.hisp.dhis.android.core.trackedentity.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.call.queries.internal.BaseQuery;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@AutoValue
abstract class TeiQuery extends BaseQuery {

    @NonNull
    abstract Collection<String> orgUnits();

    @Nullable
    abstract String program();

    @NonNull
    abstract OrganisationUnitMode ouMode();

    @Nullable
    abstract Date lastUpdatedStartDate();

    @NonNull
    abstract Collection<String> uids();

    @Nullable
    abstract EnrollmentStatus programStatus();

    @Nullable
    abstract String programStartDate();

    @NonNull
    abstract Integer limit();

    static Builder builder() {
        return new AutoValue_TeiQuery.Builder()
                .page(1)
                .pageSize(DEFAULT_PAGE_SIZE)
                .paging(true)
                .ouMode(OrganisationUnitMode.SELECTED)
                .orgUnits(Collections.emptyList())
                .uids(Collections.emptyList());
    }

    @AutoValue.Builder
    abstract static class Builder extends BaseQuery.Builder<Builder> {
        abstract Builder orgUnits(Collection<String> orgUnits);

        abstract Builder program(String program);

        abstract Builder ouMode(OrganisationUnitMode ouMode);

        abstract Builder lastUpdatedStartDate(Date lastUpdatedStartDate);

        abstract Builder uids(Collection<String> uIds);

        abstract Builder programStatus(EnrollmentStatus programStatus);

        abstract Builder programStartDate(String programStartDate);

        abstract Builder limit(Integer limit);

        abstract TeiQuery autoBuild();

        //Auxiliary fields
        abstract String program();

        public TeiQuery build() {
            if (program() == null) {
                programStatus(null);
            }
            return autoBuild();
        }
    }
}
