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

import com.google.common.base.Joiner;

import org.hisp.dhis.android.core.arch.dateformat.internal.SafeDateFormat;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.event.EventTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo.Columns;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo;
import org.hisp.dhis.android.core.user.AuthenticatedUserTableInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hisp.dhis.android.core.common.IdentifiableColumns.CREATED;
import static org.hisp.dhis.android.core.common.IdentifiableColumns.LAST_UPDATED;
import static org.hisp.dhis.android.core.common.IdentifiableColumns.NAME;
import static org.hisp.dhis.android.core.common.IdentifiableColumns.UID;
import static org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo.Columns.INCIDENT_DATE;
import static org.hisp.dhis.android.core.event.EventTableInfo.Columns.DUE_DATE;
import static org.hisp.dhis.android.core.event.EventTableInfo.Columns.EVENT_DATE;

@SuppressWarnings({
        "PMD.GodClass",
        "PMD.TooManyStaticImports",
        "PMD.CyclomaticComplexity",
        "PMD.StdCyclomaticComplexity"})
final class TrackedEntityInstanceLocalQueryHelper {

    private static final SafeDateFormat QUERY_FORMAT = new SafeDateFormat("yyyy-MM-dd");

    private static String TEI_ALIAS = "tei";
    private static String ENROLLMENT_ALIAS = "en";
    private static String EVENT_ALIAS = "ev";
    private static String ORGUNIT_ALIAS = "ou";
    private static String TEAV_ALIAS = "teav";

    private static String TEI_UID = dot(TEI_ALIAS, "uid");
    private static String TEI_ALL = dot(TEI_ALIAS, "*");
    private static String TEI_LAST_UPDATED = dot(TEI_ALIAS, "lastUpdated");

    private static String ENROLLMENT_DATE = EnrollmentTableInfo.Columns.ENROLLMENT_DATE;
    private static String PROGRAM = EnrollmentTableInfo.Columns.PROGRAM;

    private static String TRACKED_ENTITY_ATTRIBUTE =
            TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE;
    private static String TRACKED_ENTITY_INSTANCE =
            TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE;

    private TrackedEntityInstanceLocalQueryHelper() {
    }

    @SuppressWarnings({"PMD.UseStringBufferForStringAppends", "PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    static String getSqlQuery(TrackedEntityInstanceQueryRepositoryScope scope, List<String> excludeList, int limit) {

        String queryStr = "SELECT DISTINCT " + TEI_ALL + " FROM " +
                TrackedEntityInstanceTableInfo.TABLE_INFO.name() + " " + TEI_ALIAS;

        WhereClauseBuilder where = new WhereClauseBuilder();

        if (hasProgram(scope)) {
            queryStr += String.format(" JOIN %s %s ON %s = %s",
                    EnrollmentTableInfo.TABLE_INFO.name(), ENROLLMENT_ALIAS,
                    dot(TEI_ALIAS, UID),
                    dot(ENROLLMENT_ALIAS, EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE));

            appendProgramWhere(where, scope);

            if (hasEvent(scope)) {
                queryStr += String.format(" JOIN %s %s ON %s = %s",
                        EventTableInfo.TABLE_INFO.name(), EVENT_ALIAS,
                        dot(ENROLLMENT_ALIAS, UID),
                        dot(EVENT_ALIAS, EventTableInfo.Columns.ENROLLMENT));

                appendEventWhere(where, scope);
            }
        }

        if (hasOrgunits(scope)) {
            queryStr += String.format(" JOIN %s %s ON %s = %s",
                    OrganisationUnitTableInfo.TABLE_INFO.name(), ORGUNIT_ALIAS,
                    dot(TEI_ALIAS, TrackedEntityInstanceTableInfo.Columns.ORGANISATION_UNIT),
                    dot(ORGUNIT_ALIAS, UID));

            appendOrgunitWhere(where, scope);
        }

        if (scope.trackedEntityType() != null) {
            where.appendKeyStringValue(dot(TEI_ALIAS, TrackedEntityInstanceTableInfo.Columns.TRACKED_ENTITY_TYPE),
                    escapeQuotes(scope.trackedEntityType()));
        }

        if (scope.states() == null) {
            where.appendNotKeyStringValue(dot(TEI_ALIAS, DataColumns.STATE), State.RELATIONSHIP.name());
        } else {
            where.appendInKeyEnumValues(dot(TEI_ALIAS, DataColumns.STATE), scope.states());
        }

        if (scope.includeDeleted() == null || !scope.includeDeleted()) {
            where.appendKeyOperatorValue(dot(TEI_ALIAS, TrackedEntityInstanceTableInfo.Columns.DELETED), "!=", "1");
        }

        appendQueryWhere(where, scope);
        appendFiltersWhere(where, scope);
        appendExcludeList(where, excludeList);

        if (!where.isEmpty()) {
            queryStr += " WHERE " + where.build();
        }

        queryStr += orderByClause(scope);

        if (limit > 0) {
            queryStr += " LIMIT " + limit;
        }

        return queryStr;
    }

    private static boolean hasProgram(TrackedEntityInstanceQueryRepositoryScope scope) {
        return scope.program() != null;
    }

    private static boolean hasEvent(TrackedEntityInstanceQueryRepositoryScope scope) {
        return scope.assignedUserMode() != null || scope.eventStatus() != null ||
                scope.eventStartDate() != null || scope.eventEndDate() != null;
    }

    private static void appendProgramWhere(WhereClauseBuilder where, TrackedEntityInstanceQueryRepositoryScope scope) {
        if (scope.program() != null) {
            where.appendKeyStringValue(dot(ENROLLMENT_ALIAS, PROGRAM), escapeQuotes(scope.program()));
        }
        if (scope.programStartDate() != null) {
            where.appendKeyGreaterOrEqStringValue(dot(ENROLLMENT_ALIAS, ENROLLMENT_DATE),
                    scope.formattedProgramStartDate());
        }
        if (scope.programEndDate() != null) {
            where.appendKeyLessThanOrEqStringValue(dot(ENROLLMENT_ALIAS, ENROLLMENT_DATE),
                    scope.formattedProgramEndDate());
        }
        if (scope.enrollmentStatus() != null) {
            where.appendInKeyEnumValues(dot(ENROLLMENT_ALIAS, EnrollmentTableInfo.Columns.STATUS),
                    scope.enrollmentStatus());
        }
        if (scope.includeDeleted() == null || !scope.includeDeleted()) {
            where.appendKeyOperatorValue(dot(ENROLLMENT_ALIAS, EnrollmentTableInfo.Columns.DELETED), "!=", "1");
        }
    }

    private static boolean hasOrgunits(TrackedEntityInstanceQueryRepositoryScope scope) {
        return !scope.orgUnits().isEmpty()
                && !OrganisationUnitMode.ALL.equals(scope.orgUnitMode())
                && !OrganisationUnitMode.ACCESSIBLE.equals(scope.orgUnitMode())
                || hasOrgunitSortOrder(scope);
    }

    private static boolean hasOrgunitSortOrder(TrackedEntityInstanceQueryRepositoryScope scope) {
        for (TrackedEntityInstanceQueryScopeOrderByItem order : scope.order()) {
            if (order.column().equals(TrackedEntityInstanceQueryScopeOrderColumn.ORGUNIT_NAME)) {
                return true;
            }
        }
        return false;
    }

    private static void appendOrgunitWhere(WhereClauseBuilder where, TrackedEntityInstanceQueryRepositoryScope scope) {
        OrganisationUnitMode ouMode = scope.orgUnitMode() == null ? OrganisationUnitMode.SELECTED : scope.orgUnitMode();

        WhereClauseBuilder inner = new WhereClauseBuilder();
        switch (ouMode) {
            case DESCENDANTS:
                for (String orgunit : scope.orgUnits()) {
                    inner.appendOrKeyLikeStringValue(dot(ORGUNIT_ALIAS, Columns.PATH), "%" +
                            escapeQuotes(orgunit) + "%");
                }
                break;
            case CHILDREN:
                for (String orgunit : scope.orgUnits()) {
                    inner.appendOrKeyStringValue(dot(ORGUNIT_ALIAS, Columns.PARENT), escapeQuotes(orgunit));
                    // TODO Include orgunit?
                    inner.appendOrKeyStringValue(dot(ORGUNIT_ALIAS, UID), escapeQuotes(orgunit));
                }
                break;
            // SELECTED mode
            default:
                for (String orgunit : scope.orgUnits()) {
                    inner.appendOrKeyStringValue(dot(ORGUNIT_ALIAS, UID), escapeQuotes(orgunit));
                }
                break;
        }

        if (!inner.isEmpty()) {
            where.appendComplexQuery(inner.build());
        }
    }

    private static void appendQueryWhere(WhereClauseBuilder where, TrackedEntityInstanceQueryRepositoryScope scope) {
        if (scope.query() != null) {
            String[] tokens = scope.query().value().split(" ");
            for (String token : tokens) {
                String valueStr = scope.query().operator().equals(FilterItemOperator.LIKE) ? "%" + escapeQuotes(token)
                        + "%" : escapeQuotes(token);
                String sub = String.format("SELECT 1 FROM %s %s WHERE %s = %s AND %s %s '%s'",
                        TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(), TEAV_ALIAS,
                        dot(TEAV_ALIAS, TRACKED_ENTITY_INSTANCE), dot(TEI_ALIAS, UID),
                        dot(TEAV_ALIAS, TrackedEntityAttributeValueTableInfo.Columns.VALUE),
                        scope.query().operator().getSqlOperator(), valueStr);
                where.appendExistsSubQuery(sub);
            }
        }
    }

    private static void appendFiltersWhere(WhereClauseBuilder where, TrackedEntityInstanceQueryRepositoryScope scope) {
        // TODO Filter by program attributes in case program is provided

        appendFilterWhere(where, scope.filter());
        appendFilterWhere(where, scope.attribute());
    }

    private static void appendFilterWhere(WhereClauseBuilder where, List<RepositoryScopeFilterItem> items) {
        for (RepositoryScopeFilterItem item : items) {
            String valueStr = item.operator().equals(FilterItemOperator.LIKE) ? "%" + escapeQuotes(item.value())
                    + "%" : escapeQuotes(item.value());
            String sub = String.format("SELECT 1 FROM %s %s WHERE %s = %s AND %s = '%s' AND %s %s '%s'",
                    TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(), TEAV_ALIAS,
                    dot(TEAV_ALIAS, TRACKED_ENTITY_INSTANCE), dot(TEI_ALIAS, UID),
                    dot(TEAV_ALIAS, TRACKED_ENTITY_ATTRIBUTE), escapeQuotes(item.key()),
                    dot(TEAV_ALIAS, TrackedEntityAttributeValueTableInfo.Columns.VALUE),
                    item.operator().getSqlOperator(), valueStr);
            where.appendExistsSubQuery(sub);
        }
    }

    private static void appendExcludeList(WhereClauseBuilder where, List<String> excludeList) {
        if (excludeList != null && !excludeList.isEmpty()) {
            where.appendNotInKeyStringValues(TEI_UID, excludeList);
        }
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    private static void appendEventWhere(WhereClauseBuilder where, TrackedEntityInstanceQueryRepositoryScope scope) {
        if (scope.assignedUserMode() != null) {
            appendAssignedUserMode(where, scope);
        }
        if (scope.eventStatus() == null) {
            appendEventDates(where, scope, EVENT_DATE);
        } else if (scope.eventStatus().size() > 0 && scope.eventStartDate() != null && scope.eventEndDate() != null) {
            String nowStr = QUERY_FORMAT.format(new Date());
            WhereClauseBuilder statusListWhere = new WhereClauseBuilder();
            for (EventStatus eventStatus : scope.eventStatus()) {
                WhereClauseBuilder statusWhere = new WhereClauseBuilder();
                switch (eventStatus) {
                    case ACTIVE:
                    case COMPLETED:
                    case VISITED:
                        statusWhere.appendKeyStringValue(dot(EVENT_ALIAS, EventTableInfo.Columns.STATUS), eventStatus);
                        appendEventDates(statusWhere, scope, EVENT_DATE);
                        break;
                    case SCHEDULE:
                        appendEventDates(statusWhere, scope, DUE_DATE);
                        statusWhere.appendIsNullValue(EVENT_DATE);
                        statusWhere.appendIsNotNullValue(dot(EVENT_ALIAS, EventTableInfo.Columns.STATUS));
                        statusWhere.appendKeyGreaterOrEqStringValue(dot(EVENT_ALIAS, DUE_DATE), nowStr);
                        break;
                    case OVERDUE:
                        appendEventDates(statusWhere, scope, DUE_DATE);
                        statusWhere.appendIsNullValue(EVENT_DATE);
                        statusWhere.appendIsNotNullValue(dot(EVENT_ALIAS, EventTableInfo.Columns.STATUS));
                        statusWhere.appendKeyLessThanStringValue(dot(EVENT_ALIAS, DUE_DATE), nowStr);
                        break;
                    case SKIPPED:
                        statusWhere.appendKeyStringValue(dot(EVENT_ALIAS, EventTableInfo.Columns.STATUS), eventStatus);
                        appendEventDates(statusWhere, scope, DUE_DATE);
                        break;
                    default:
                        break;
                }
                statusListWhere.appendOrComplexQuery(statusWhere.build());
            }
            where.appendComplexQuery(statusListWhere.build());
        }
        where.appendKeyOperatorValue(dot(EVENT_ALIAS, EventTableInfo.Columns.DELETED), "!=", "1");
    }

    private static void appendEventDates(WhereClauseBuilder where,
                                         TrackedEntityInstanceQueryRepositoryScope scope,
                                         String targetDate) {
        if (scope.eventStartDate() != null) {
            where.appendKeyGreaterOrEqStringValue(dot(EVENT_ALIAS, targetDate), scope.formattedEventStartDate());
        }
        if (scope.eventEndDate() != null) {
            where.appendKeyLessThanOrEqStringValue(dot(EVENT_ALIAS, targetDate), scope.formattedEventEndDate());
        }
    }

    private static void appendAssignedUserMode(WhereClauseBuilder where,
                                               TrackedEntityInstanceQueryRepositoryScope scope) {
        AssignedUserMode mode = scope.assignedUserMode();
        if (mode == null) {
            return;
        }

        String assignedUserColumn = dot(EVENT_ALIAS, EventTableInfo.Columns.ASSIGNED_USER);
        switch (mode) {
            case CURRENT:
                String subquery = String.format("(SELECT %s FROM %s LIMIT 1)",
                        AuthenticatedUserTableInfo.Columns.USER,
                        AuthenticatedUserTableInfo.TABLE_INFO.name());
                where.appendKeyOperatorValue(assignedUserColumn, "IN", subquery);
                break;
            case ANY:
                where.appendIsNotNullValue(assignedUserColumn);
                break;
            case NONE:
                where.appendIsNullValue(assignedUserColumn);
                break;
            default:
                break;
        }
        where.appendKeyOperatorValue(dot(EVENT_ALIAS, EventTableInfo.Columns.DELETED), "!=", "1");
    }

    @SuppressWarnings({
            "PMD.CyclomaticComplexity",
            "PMD.StdCyclomaticComplexity"})
    private static String orderByClause(TrackedEntityInstanceQueryRepositoryScope scope) {
        List<String> orderClauses = new ArrayList<>();
        for (TrackedEntityInstanceQueryScopeOrderByItem item : scope.order()) {
            switch (item.column().type()) {
                case CREATED:
                    if (hasProgram(scope)) {
                        orderClauses.add(orderByEnrollmentField(scope.program(), CREATED, item.direction()));
                    } else {
                        orderClauses.add(dot(TEI_ALIAS, CREATED) + " " + item.direction().name());
                    }
                    break;
                case LAST_UPDATED:
                    if (hasProgram(scope)) {
                        orderClauses.add(orderByEnrollmentField(scope.program(), LAST_UPDATED, item.direction()));
                    } else {
                        orderClauses.add(dot(TEI_ALIAS, LAST_UPDATED) + " " + item.direction().name());
                    }
                    break;
                case ORGUNIT_NAME:
                    orderClauses.add(dot(ORGUNIT_ALIAS, NAME) + " " + item.direction().name());
                    break;
                case ATTRIBUTE:
                    // Trick to put null values at the end of the list
                    String attOrder = String.format("IFNULL((SELECT %s FROM %s WHERE %s = %s AND %s = %s), 'zzzzzzzz')",
                            TrackedEntityAttributeValueTableInfo.Columns.VALUE,
                            TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(),
                            TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                            CollectionsHelper.withSingleQuotationMarks(item.column().value()),
                            TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                            dot(TEI_ALIAS, UID));
                    orderClauses.add(attOrder + " " + item.direction().name());
                    break;
                case ENROLLMENT_DATE:
                    orderClauses.add(orderByEnrollmentField(scope.program(), ENROLLMENT_DATE, item.direction()));
                    break;
                case INCIDENT_DATE:
                    orderClauses.add(orderByEnrollmentField(scope.program(), INCIDENT_DATE, item.direction()));
                    break;
                case ENROLLMENT_STATUS:
                    orderClauses.add(orderByEnrollmentField(scope.program(), EnrollmentTableInfo.Columns.STATUS,
                            item.direction()));
                    break;
                case EVENT_DATE:
                    String eventField =
                            "IFNULL(" + EVENT_DATE + "," + DUE_DATE + ")";
                    orderClauses.add(orderByEventField(scope.program(), eventField, item.direction()));
                    break;
                case COMPLETION_DATE:
                    orderClauses.add(orderByEventField(scope.program(), EventTableInfo.Columns.COMPLETE_DATE,
                            item.direction()));
                    break;
                default:
                    break;
            }
        }
        orderClauses.add(getOrderByLastUpdated());

        return " ORDER BY " + Joiner.on(", ").join(orderClauses);
    }

    private static String getOrderByLastUpdated() {
        // TODO In case a program uid is provided, the server orders by enrollmentStatus.
        return TEI_LAST_UPDATED + " DESC ";
    }

    private static String orderByEnrollmentField(String program, String field, RepositoryScope.OrderByDirection dir) {
        String programClause = program == null ? "" :
                "AND " + EnrollmentTableInfo.Columns.PROGRAM + " = '" + program + "'";
        return String.format(
                "IFNULL((SELECT %s FROM %s WHERE %s = %s %s ORDER BY %s DESC LIMIT 1), 'zzzzz') %s",
                field,
                EnrollmentTableInfo.TABLE_INFO.name(),
                EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                dot(TEI_ALIAS, UID),
                programClause,
                EnrollmentTableInfo.Columns.ENROLLMENT_DATE,
                dir.name());
    }

    private static String orderByEventField(String program, String field, RepositoryScope.OrderByDirection dir) {
        String programClause = program == null ? "" :
                "AND " + EnrollmentTableInfo.Columns.PROGRAM + " = '" + program + "'";
        return String.format(
                "(SELECT %s FROM %s WHERE %s IN (SELECT %s FROM %s WHERE %s = %s %s) " +
                        "ORDER BY IFNULL(%s, %s) DESC LIMIT 1) %s",
                field,
                EventTableInfo.TABLE_INFO.name(),
                EventTableInfo.Columns.ENROLLMENT,
                UID,
                EnrollmentTableInfo.TABLE_INFO.name(),
                EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                dot(TEI_ALIAS, UID),
                programClause,
                EVENT_DATE, DUE_DATE,
                dir.name());
    }

    private static String dot(String item1, String item2) {
        return item1 + "." + item2;
    }

    private static String escapeQuotes(String value) {
        return value.replaceAll("'", "''");
    }
}