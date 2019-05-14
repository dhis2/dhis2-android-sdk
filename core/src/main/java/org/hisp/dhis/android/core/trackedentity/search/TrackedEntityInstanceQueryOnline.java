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

package org.hisp.dhis.android.core.trackedentity.search;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;
import org.hisp.dhis.android.core.common.SafeDateFormat;
import org.hisp.dhis.android.core.data.api.OuMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@AutoValue
abstract class TrackedEntityInstanceQueryOnline extends BaseQuery {

    private static final SafeDateFormat QUERY_FORMAT = new SafeDateFormat("yyyy-MM-dd");

    @NonNull
    abstract List<String> orgUnits();

    @Nullable
    abstract OuMode orgUnitMode();

    @Nullable
    abstract String program();

    @Nullable
    abstract String query();

    @Nullable
    abstract List<String> attribute();

    @Nullable
    abstract List<String> filter();

    @Nullable
    abstract Date programStartDate();

    @Nullable
    abstract Date programEndDate();

    String formattedProgramStartDate() {
        return programStartDate() == null ? null : QUERY_FORMAT.format(programStartDate());
    }

    String formattedProgramEndDate() {
        return programEndDate() == null ? null : QUERY_FORMAT.format(programEndDate());
    }

    static Builder builder() {
        return new AutoValue_TrackedEntityInstanceQueryOnline.Builder();
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    static TrackedEntityInstanceQueryOnline create(TrackedEntityInstanceQuery teiQuery) {
        List<String> attributes = new ArrayList<>();
        for (QueryItem item : teiQuery.attribute()) {
            StringBuilder attributeBuilder = new StringBuilder(item.item());
            for (QueryFilter filter : item.filters()) {
                attributeBuilder.append(':').append(filter.operator()).append(':').append(filter.filter());
            }
            attributes.add(attributeBuilder.toString());
        }

        List<String> filters = new ArrayList<>();
        for (QueryItem item : teiQuery.filter()) {
            StringBuilder filterBuilder = new StringBuilder(item.item());
            for (QueryFilter filter : item.filters()) {
                filterBuilder.append(':').append(filter.operator()).append(':').append(filter.filter());
            }
            filters.add(filterBuilder.toString());
        }

        String query = null;
        if (teiQuery.query() != null && teiQuery.query().filter() != null) {
            query = teiQuery.query().operator().name() + ":" + teiQuery.query().filter();
        }

        return TrackedEntityInstanceQueryOnline.builder()
                .query(query)
                .attribute(attributes)
                .filter(filters)
                .orgUnits(teiQuery.orgUnits())
                .orgUnitMode(teiQuery.orgUnitMode())
                .program(teiQuery.program())
                .programStartDate(teiQuery.programStartDate())
                .programEndDate(teiQuery.programEndDate())
                .page(teiQuery.page())
                .pageSize(teiQuery.pageSize())
                .paging(teiQuery.paging())
                .build();
    }

    @AutoValue.Builder
    abstract static class Builder extends BaseQuery.Builder<Builder> {
        abstract Builder orgUnits(List<String> orgUnits);

        abstract Builder orgUnitMode(OuMode orgUnitMode);

        abstract Builder program(String program);

        abstract Builder query(String query);

        abstract Builder attribute(List<String> attribute);

        abstract Builder filter(List<String> filter);

        abstract Builder programStartDate(Date programStartDate);

        abstract Builder programEndDate(Date programEndDate);

        abstract TrackedEntityInstanceQueryOnline build();
    }
}