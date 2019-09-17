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

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo.Columns;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFields;

import java.util.List;

import static org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel.Columns.UID;
import static org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueFields.VALUE;

final class TrackedEntityInstanceLocalQueryHelper {

    private static String TEI_ALIAS = "tei";
    private static String ENROLLMENT_ALIAS = "en";
    private static String ORGUNIT_ALIAS = "ou";
    private static String TEAV_ALIAS = "teav";

    private static String TEI_UID = dot(TEI_ALIAS, "uid");
    private static String TEI_ALL = dot(TEI_ALIAS, "*");
    private static String TEI_STATE = dot(TEI_ALIAS, BaseDataModel.Columns.STATE);
    private static String TEI_LAST_UPDATED = dot(TEI_ALIAS, "lastUpdated");

    private static String ENROLLMENT_DATE = EnrollmentTableInfo.Columns.ENROLLMENT_DATE;
    private static String PROGRAM = EnrollmentTableInfo.Columns.PROGRAM;

    private static String TRACKED_ENTITY_ATTRIBUTE =
            TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE;
    private static String TRACKED_ENTITY_INSTANCE =
            TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE;

    private TrackedEntityInstanceLocalQueryHelper() { }

    @SuppressWarnings({"PMD.UseStringBufferForStringAppends"})
    static String getSqlQuery(TrackedEntityInstanceQueryRepositoryScope scope, List<String> excludeList, int limit) {

        String queryStr = "SELECT DISTINCT " + TEI_ALL + " FROM " +
                TrackedEntityInstanceTableInfo.TABLE_INFO.name() + " " + TEI_ALIAS;

        WhereClauseBuilder where = new WhereClauseBuilder();

        if (hasProgram(scope)) {
            queryStr += String.format(" JOIN %s %s ON %s = %s",
                    EnrollmentTableInfo.TABLE_INFO.name(), ENROLLMENT_ALIAS,
                    dot(TEI_ALIAS, UID),
                    dot(ENROLLMENT_ALIAS, "trackedentityinstance"));

            appendProgramWhere(where, scope);
        }

        if (hasOrgunits(scope)) {
            queryStr += String.format(" JOIN %s %s ON %s = %s",
                    OrganisationUnitTableInfo.TABLE_INFO.name(), ORGUNIT_ALIAS,
                    dot(TEI_ALIAS, "organisationUnit"),
                    dot(ORGUNIT_ALIAS, UID));

            appendOrgunitWhere(where, scope);
        }

        if (scope.trackedEntityType() != null) {
            where.appendKeyStringValue(dot(TEI_ALIAS, TrackedEntityInstanceFields.TRACKED_ENTITY_TYPE),
                    scope.trackedEntityType());
        }

        if (scope.states() != null) {
            where.appendInKeyEnumValues(dot(TEI_ALIAS, BaseDataModel.Columns.STATE), scope.states());
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

        // TODO In case a program uid is provided, the server orders by enrollmentStatus.

        queryStr += " ORDER BY CASE " +
                "WHEN " + TEI_STATE + " IN ('" + State.TO_POST + "','" + State.TO_UPDATE + "') THEN 1 " +
                "WHEN " + TEI_STATE + " = '" + State.SYNCED + "' THEN 2 ELSE 3 END ASC, " +
                TEI_LAST_UPDATED + " DESC ";

        if (limit > 0) {
            queryStr += " LIMIT " + limit;
        }

        return queryStr;
    }

    private static boolean hasProgram(TrackedEntityInstanceQueryRepositoryScope scope) {
        return scope.program() != null;
    }

    private static void appendProgramWhere(WhereClauseBuilder where, TrackedEntityInstanceQueryRepositoryScope scope) {
        if (scope.program() != null) {
            where.appendKeyStringValue(dot(ENROLLMENT_ALIAS, PROGRAM), scope.program());
        }
        if (scope.programStartDate() != null) {
            where.appendKeyGreaterOrEqStringValue(dot(ENROLLMENT_ALIAS, ENROLLMENT_DATE),
                    scope.formattedProgramStartDate());
        }
        if (scope.programEndDate() != null) {
            where.appendKeyLessThanOrEqStringValue(dot(ENROLLMENT_ALIAS, ENROLLMENT_DATE),
                    scope.formattedProgramEndDate());
        }
        if (scope.includeDeleted() == null || !scope.includeDeleted()) {
            where.appendKeyOperatorValue(dot(ENROLLMENT_ALIAS, EnrollmentTableInfo.Columns.DELETED), "!=", "1");
        }
    }

    private static boolean hasOrgunits(TrackedEntityInstanceQueryRepositoryScope scope) {
        return !scope.orgUnits().isEmpty() &&
                !OrganisationUnitMode.ALL.equals(scope.orgUnitMode()) &&
                !OrganisationUnitMode.ACCESSIBLE.equals(scope.orgUnitMode());
    }

    private static void appendOrgunitWhere(WhereClauseBuilder where, TrackedEntityInstanceQueryRepositoryScope scope) {
        OrganisationUnitMode ouMode = scope.orgUnitMode() == null ? OrganisationUnitMode.SELECTED : scope.orgUnitMode();

        WhereClauseBuilder inner = new WhereClauseBuilder();
        switch (ouMode) {
            case DESCENDANTS:
                for (String orgunit : scope.orgUnits()) {
                    inner.appendOrKeyLikeStringValue(dot(ORGUNIT_ALIAS, Columns.PATH), "%" + orgunit + "%");
                }
                break;
            case CHILDREN:
                for (String orgunit : scope.orgUnits()) {
                    inner.appendOrKeyStringValue(dot(ORGUNIT_ALIAS, Columns.PARENT), orgunit);
                    // TODO Include orgunit?
                    inner.appendOrKeyStringValue(dot(ORGUNIT_ALIAS, UID), orgunit);
                }
                break;
            // SELECTED mode
            default:
                for (String orgunit : scope.orgUnits()) {
                    inner.appendOrKeyStringValue(dot(ORGUNIT_ALIAS, UID), orgunit);
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
                String valueStr = scope.query().operator().equals(FilterItemOperator.LIKE) ? "%" + token + "%" : token;
                String sub = String.format("SELECT 1 FROM %s %s WHERE %s = %s AND %s %s '%s'",
                        TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(), TEAV_ALIAS,
                        dot(TEAV_ALIAS, TRACKED_ENTITY_INSTANCE), dot(TEI_ALIAS, UID),
                        dot(TEAV_ALIAS, VALUE), scope.query().operator().getSqlOperator(), valueStr);
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
            String valueStr = item.operator().equals(FilterItemOperator.LIKE) ? "%" + item.value() + "%" : item.value();
            String sub = String.format("SELECT 1 FROM %s %s WHERE %s = %s AND %s = '%s' AND %s %s '%s'",
                    TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(), TEAV_ALIAS,
                    dot(TEAV_ALIAS, TRACKED_ENTITY_INSTANCE), dot(TEI_ALIAS, UID),
                    dot(TEAV_ALIAS, TRACKED_ENTITY_ATTRIBUTE), item.key(),
                    dot(TEAV_ALIAS, VALUE), item.operator().getSqlOperator(), valueStr);
            where.appendExistsSubQuery(sub);
        }
    }

    private static void appendExcludeList(WhereClauseBuilder where, List<String> excludeList) {
        if (excludeList != null && !excludeList.isEmpty()) {
            where.appendNotInKeyStringValues(TEI_UID, excludeList);
        }
    }

    private static String dot(String item1, String item2) {
        return item1 + "." + item2;
    }
}