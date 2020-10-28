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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.base.Joiner;

import org.hisp.dhis.android.core.arch.call.queries.internal.BaseQuery;
import org.hisp.dhis.android.core.arch.dateformat.internal.SafeDateFormat;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoValue
abstract class TrackedEntityInstanceQueryOnline extends BaseQuery {

    private static final SafeDateFormat QUERY_FORMAT = new SafeDateFormat("yyyy-MM-dd");

    @NonNull
    abstract List<String> orgUnits();

    @Nullable
    abstract OrganisationUnitMode orgUnitMode();

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

    @Nullable
    abstract EnrollmentStatus enrollmentStatus();

    @Nullable
    abstract EventStatus eventStatus();

    @Nullable
    abstract Date eventStartDate();

    @Nullable
    abstract Date eventEndDate();

    @Nullable
    abstract String trackedEntityType();

    //TODO It is not used in the query because it modifies returned grid structure: if true, it adds an extra column
    @NonNull
    abstract Boolean includeDeleted();

    @Nullable
    abstract AssignedUserMode assignedUserMode();

    @Nullable
    abstract String order();

    String formattedProgramStartDate() {
        return formatDate(programStartDate());
    }

    String formattedProgramEndDate() {
        return formatDate(programEndDate());
    }

    String formattedEventStartDate() {
        return formatDate(eventStartDate());
    }

    String formattedEventEndDate() {
        return formatDate(eventEndDate());
    }

    private String formatDate(Date date) {
        return date == null ? null : QUERY_FORMAT.format(date);
    }

    abstract Builder toBuilder();

    static Builder builder() {
        return new AutoValue_TrackedEntityInstanceQueryOnline.Builder();
    }

    static TrackedEntityInstanceQueryOnline create(TrackedEntityInstanceQueryRepositoryScope scope) {
        String query = null;
        if (scope.query() != null) {
            query = scope.query().operator().getApiUpperOperator() + ":" + scope.query().value();
        }

        // EnrollmentStatus does not accepts a list of status but a single value in web API.
        EnrollmentStatus enrollmentStatus = scope.enrollmentStatus() == null || scope.enrollmentStatus().isEmpty() ?
                null : scope.enrollmentStatus().get(0);

        EventStatus eventStatus = getEventStatus(scope);

        return TrackedEntityInstanceQueryOnline.builder()
                .query(query)
                .attribute(toAPIFilterFormat(scope.attribute()))
                .filter(toAPIFilterFormat(scope.filter()))
                .orgUnits(scope.orgUnits())
                .orgUnitMode(scope.orgUnitMode())
                .program(scope.program())
                .programStartDate(scope.programStartDate())
                .programEndDate(scope.programEndDate())
                .enrollmentStatus(enrollmentStatus)
                .eventStatus(eventStatus)
                .eventStartDate(scope.eventStartDate())
                .eventEndDate(scope.eventEndDate())
                .trackedEntityType(scope.trackedEntityType())
                .includeDeleted(false)
                .assignedUserMode(scope.assignedUserMode())
                .order(toAPIOrderFormat(scope.order()))
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

    private static String toAPIOrderFormat(List<TrackedEntityInstanceQueryScopeOrderByItem> orders) {
        List<String> orderList = new ArrayList<>();
        for (TrackedEntityInstanceQueryScopeOrderByItem order : orders) {
            String apiString  = order.toAPIString();
            if (apiString != null) {
                orderList.add(apiString);
            }
        }
        return Joiner.on(',').join(orderList);
    }

    private static EventStatus getEventStatus(TrackedEntityInstanceQueryRepositoryScope scope) {
        // EventStatus does not accepts a list of status but a single value in web API.
        // Additionally, it requires that eventStartDate and eventEndDate are defined.
        if (scope.eventStatus() != null && scope.eventStatus().size() > 0 &&
                scope.eventStartDate() != null && scope.eventEndDate() != null) {
            return scope.eventStatus().get(0);
        }
        return null;
    }

    @AutoValue.Builder
    abstract static class Builder extends BaseQuery.Builder<Builder> {
        abstract Builder orgUnits(List<String> orgUnits);

        abstract Builder orgUnitMode(OrganisationUnitMode orgUnitMode);

        abstract Builder program(String program);

        abstract Builder query(String query);

        abstract Builder attribute(List<String> attribute);

        abstract Builder filter(List<String> filter);

        abstract Builder programStartDate(Date programStartDate);

        abstract Builder programEndDate(Date programEndDate);

        abstract Builder enrollmentStatus(EnrollmentStatus programStatus);

        abstract Builder eventStatus(EventStatus eventStatus);

        abstract Builder eventStartDate(Date eventStartDate);

        abstract Builder eventEndDate(Date eventEndDate);

        abstract Builder trackedEntityType(String trackedEntityType);

        abstract Builder includeDeleted(Boolean includeDeleted);

        abstract Builder assignedUserMode(AssignedUserMode assignedUserMode);

        abstract Builder order(String order);

        abstract TrackedEntityInstanceQueryOnline build();
    }
}