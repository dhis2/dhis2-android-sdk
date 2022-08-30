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

package org.hisp.dhis.android.core.program.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.repositories.filters.internal.UnwrappedEqInFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.settings.EnrollmentScope;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@AutoValue
public abstract class ProgramDataDownloadParams {

    public static final Integer DEFAULT_LIMIT = 500;

    @NonNull
    public abstract List<String> uids();

    @NonNull
    public abstract List<String> orgUnits();

    @Nullable
    public abstract OrganisationUnitMode orgUnitMode();

    @Nullable
    public abstract String program();

    @Nullable
    public abstract EnrollmentScope programStatus();

    @Nullable
    public abstract Date programStartDate();

    @Nullable
    public abstract Date programEndDate();

    @Nullable
    public abstract String trackedEntityType();

    @Nullable
    public abstract Boolean limitByOrgunit();

    @Nullable
    public abstract Boolean limitByProgram();

    @Nullable
    public abstract Integer limit();

    @NonNull
    public abstract Boolean overwrite();

    public static ProgramDataDownloadParams fromRepositoryScope(RepositoryScope scope) {
        Builder builder = builder();
        for (RepositoryScopeFilterItem item : scope.filters()) {
            switch (item.key()) {
                case QueryParams.UID:
                    builder.uids(UnwrappedEqInFilterConnector.getValueList(item.value()));
                    break;
                case QueryParams.PROGRAM:
                    builder.program(item.value());
                    break;
                case QueryParams.LIMIT_BY_ORGUNIT:
                    builder.limitByOrgunit(item.value().equals("1"));
                    break;
                case QueryParams.LIMIT_BY_PROGRAM:
                    builder.limitByProgram(item.value().equals("1"));
                    break;
                case QueryParams.LIMIT:
                    builder.limit(Integer.parseInt(item.value()));
                    break;
                case QueryParams.PROGRAM_STATUS:
                    builder.programStatus(EnrollmentScope.valueOf(item.value()));
                    break;
                case QueryParams.OVERWRITE:
                    builder.overwrite(item.value().equals("1"));
                    break;
                default:
            }
        }
        return builder.build();
    }

    public static Builder builder() {
        return new AutoValue_ProgramDataDownloadParams.Builder()
                .overwrite(false)
                .orgUnits(Collections.emptyList())
                .uids(Collections.emptyList());
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uids(List<String> uids);

        public abstract Builder orgUnits(List<String> orgUnits);

        public abstract Builder orgUnitMode(OrganisationUnitMode orgUnitMode);

        public abstract Builder program(String program);

        public abstract Builder programStatus(EnrollmentScope enrollmentScope);

        public abstract Builder programStartDate(Date programStartDate);

        public abstract Builder programEndDate(Date programEndDate);

        public abstract Builder trackedEntityType(String trackedEntityType);

        public abstract Builder limitByProgram(Boolean limitByProgram);

        public abstract Builder limitByOrgunit(Boolean limitByOrgunit);

        public abstract Builder limit(Integer limit);

        public abstract Builder overwrite(Boolean overwrite);

        public abstract ProgramDataDownloadParams build();
    }

    public static class QueryParams {
        public static final String UID = "uid";
        public static final String PROGRAM = "program";
        public static final String PROGRAM_STATUS = "programStatus";
        public static final String PROGRAM_START_DATE = "programStartDate";
        public static final String PROGRAM_END_DATE = "programEndDate";
        public static final String ORG_UNITS = "ou";
        public static final String ORG_UNIT_MODE = "ouMode";
        public static final String TRACKED_ENTITY_TYPE = "trackedEntityType";
        public static final String LIMIT_BY_ORGUNIT = "limitByOrgunit";
        public static final String LIMIT_BY_PROGRAM = "limitByProgram";
        public static final String LIMIT = "limit";
        public static final String OVERWRITE = "overwrite";
    }
}