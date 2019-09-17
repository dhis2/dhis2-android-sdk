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

package org.hisp.dhis.android.core.trackedentity.search.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.call.queries.internal.BaseQuery;
import org.hisp.dhis.android.core.arch.dateformat.internal.SafeDateFormat;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoValue
public abstract class TrackedEntityInstanceQueryOnline extends BaseQuery {

    private static final SafeDateFormat QUERY_FORMAT = new SafeDateFormat("yyyy-MM-dd");

    @NonNull
    public abstract List<String> orgUnits();

    @Nullable
    public abstract OrganisationUnitMode orgUnitMode();

    @Nullable
    public abstract String program();

    @Nullable
    public abstract String query();

    @Nullable
    public abstract List<String> attribute();

    @Nullable
    public abstract List<String> filter();

    @Nullable
    public abstract Date programStartDate();

    @Nullable
    public abstract Date programEndDate();

    @Nullable
    public abstract String trackedEntityType();

    @NonNull
    public abstract Boolean includeDeleted();

    String formattedProgramStartDate() {
        return programStartDate() == null ? null : QUERY_FORMAT.format(programStartDate());
    }

    String formattedProgramEndDate() {
        return programEndDate() == null ? null : QUERY_FORMAT.format(programEndDate());
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_TrackedEntityInstanceQueryOnline.Builder();
    }

    public static TrackedEntityInstanceQueryOnline create(TrackedEntityInstanceQueryRepositoryScope scope) {
        String query = null;
        if (scope.query() != null) {
            query = scope.query().operator().getApiUpperOperator() + ":" + scope.query().value();
        }

        return TrackedEntityInstanceQueryOnline.builder()
                .query(query)
                .attribute(toAPIFilterFormat(scope.attribute()))
                .filter(toAPIFilterFormat(scope.filter()))
                .orgUnits(scope.orgUnits())
                .orgUnitMode(scope.orgUnitMode())
                .program(scope.program())
                .programStartDate(scope.programStartDate())
                .programEndDate(scope.programEndDate())
                .trackedEntityType(scope.trackedEntityType())
                .includeDeleted(false)
                .page(1)
                .pageSize(50)
                .paging(true)
                .build();
    }

    private static List<String> toAPIFilterFormat(List<RepositoryScopeFilterItem> items) {
        Map<String, String> itemMap = new HashMap<>();
        for (RepositoryScopeFilterItem item : items) {
            String filterClause = ":" + item.operator().getApiUpperOperator() + ":" + item.value();
            String existingClause = itemMap.get(item.key());
            String newClause = (existingClause == null ? "" : existingClause) + filterClause;

            itemMap.put(item.key(), newClause);
        }

        List<String> itemList = new ArrayList<>();
        for (Map.Entry<String, String> entry : itemMap.entrySet()) {
            itemList.add(entry.getKey() + entry.getValue());
        }
        return itemList;
    }

    @AutoValue.Builder
    public abstract static class Builder extends BaseQuery.Builder<Builder> {
        public abstract Builder orgUnits(List<String> orgUnits);

        public abstract Builder orgUnitMode(OrganisationUnitMode orgUnitMode);

        public abstract Builder program(String program);

        public abstract Builder query(String query);

        public abstract Builder attribute(List<String> attribute);

        public abstract Builder filter(List<String> filter);

        public abstract Builder programStartDate(Date programStartDate);

        public abstract Builder programEndDate(Date programEndDate);

        public abstract Builder trackedEntityType(String trackedEntityType);

        public abstract Builder includeDeleted(Boolean includeDeleted);

        public abstract TrackedEntityInstanceQueryOnline build();
    }
}