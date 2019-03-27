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

import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo;

abstract class TrackedEntityInstanceLocalQueryHelper {

    private static String TEI_ALIAS = "tei";
    private static String ENROLLMENT_ALIAS = "en";
    private static String ORGUNIT_ALIAS = "ou";
    private static String TEAV_ALIAS = "teav";

    static String getSqlQuery(TrackedEntityInstanceQuery query) {

        String queryStr = "SELECT " + TEI_ALIAS + ".uid FROM " +
                TrackedEntityInstanceTableInfo.TABLE_INFO.name() + " " + TEI_ALIAS;

        WhereClauseBuilder where = new WhereClauseBuilder();

        if (hasProgram(query)) {
            queryStr += String.format(" JOIN %s %s ON %s.%s = %s.%s",
                    EnrollmentTableInfo.TABLE_INFO.name(), ENROLLMENT_ALIAS,
                    TEI_ALIAS, "uid",
                    ENROLLMENT_ALIAS, "trackedentityinstance");

            appendProgramWhere(where, query);
        }

        if (hasOrgunits(query)) {
            queryStr += String.format(" JOIN %s %s ON %s.%s = %s.%s",
                    OrganisationUnitTableInfo.TABLE_INFO.name(), ORGUNIT_ALIAS,
                    TEI_ALIAS, "organisationUnit",
                    ORGUNIT_ALIAS, "uid");

            appendOrgunitWhere(where, query);
        }

        appendQueryWhere(where, query);
        appendFiltersWhere(where, query);

        if (!where.isEmpty()) {
            queryStr += " WHERE " + where.build();
        }

        // TODO Order by program status if program is present

        queryStr += " ORDER BY " + TEI_ALIAS + ".lastUpdated";

        return queryStr;
    }

    private static boolean hasProgram(TrackedEntityInstanceQuery query) {
        return query.program() != null || query.programStartDate() != null || query.programEndDate() != null;
    }

    private static void appendProgramWhere(WhereClauseBuilder where, TrackedEntityInstanceQuery query) {
        if (query.program() != null) {
            where.appendKeyStringValue(ENROLLMENT_ALIAS + ".program", query.program());
        }
        if (query.programStartDate() != null) {
            where.appendKeyGreaterOrEqStringValue(ENROLLMENT_ALIAS + ".enrollmentdate", query.formattedProgramStartDate());
        }
        if (query.programEndDate() != null) {
            where.appendKeyLessThanOrEqStringValue(ENROLLMENT_ALIAS + ".enrollmentdate", query.formattedProgramEndDate());
        }
    }

    private static boolean hasOrgunits(TrackedEntityInstanceQuery query) {
        return !query.orgUnits().isEmpty() &&
                !OuMode.ALL.equals(query.orgUnitMode()) &&
                !OuMode.ACCESSIBLE.equals(query.orgUnitMode());
    }

    private static void appendOrgunitWhere(WhereClauseBuilder where, TrackedEntityInstanceQuery query) {
        OuMode ouMode = query.orgUnitMode() == null ? OuMode.SELECTED : query.orgUnitMode();

        WhereClauseBuilder inner = new WhereClauseBuilder();
        switch (ouMode) {
            case SELECTED:
                for (String orgunit : query.orgUnits()) {
                    inner.appendOrKeyStringValue(ORGUNIT_ALIAS + ".uid", orgunit);
                }

                break;
            case DESCENDANTS:
                for (String orgunit : query.orgUnits()) {
                    inner.appendOrKeyLikeStringValue(ORGUNIT_ALIAS + ".path", "%" + orgunit + "%");
                }
                break;
            case CHILDREN:
                for (String orgunit : query.orgUnits()) {
                    inner.appendOrKeyStringValue(ORGUNIT_ALIAS + ".parent", orgunit);
                    // TODO Include orgunit?
                    inner.appendOrKeyStringValue(ORGUNIT_ALIAS + ".uid", orgunit);
                }
                break;
        }

        if (!inner.isEmpty()) {
            where.appendComplexQuery(inner.build());
        }
    }

    private static void appendQueryWhere(WhereClauseBuilder where, TrackedEntityInstanceQuery query) {
        if (query.query() != null) {
            for (String filterStr : query.query().getSqlFilters()) {
                String sub = String.format("SELECT 1 FROM %s %s WHERE %s.%s = %s.%s AND %s.%s %s '%s'",
                        TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(), TEAV_ALIAS,
                        TEAV_ALIAS, "trackedEntityInstance", TEI_ALIAS, "uid",
                        TEAV_ALIAS, "value", query.query().operator().getSqlOperator(), filterStr);
                where.appendExistsSubQuery(sub);
            }
        }
    }

    private static void appendFiltersWhere(WhereClauseBuilder where, TrackedEntityInstanceQuery query) {
        // TODO Filter by program attributes in case program is provided

        for (QueryItem item : query.filter()) {
            if (!item.filters().isEmpty()) {
                appendFilterWhere(where, item);
            }
        }
        for (QueryItem item : query.attribute()) {
            if (!item.filters().isEmpty()) {
                appendFilterWhere(where, item);
            }
        }
    }

    private static void appendFilterWhere(WhereClauseBuilder where, QueryItem item) {
        for (QueryFilter filter : item.filters()) {
            String sub = String.format("SELECT 1 FROM %s %s WHERE %s.%s = %s.%s AND %s.%s = '%s' AND %s.%s %s '%s'",
                    TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(), TEAV_ALIAS,
                    TEAV_ALIAS, "trackedEntityInstance", TEI_ALIAS, "uid",
                    TEAV_ALIAS, "trackedEntityAttribute", item.item(),
                    TEAV_ALIAS, "value", filter.operator().getSqlOperator(), filter.getSqlFilter());
            where.appendExistsSubQuery(sub);
        }
    }
}