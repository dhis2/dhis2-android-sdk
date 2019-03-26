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
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo;

abstract class TrackedEntityInstanceLocalQueryHelper {

    static String getSqlQuery(TrackedEntityInstanceQuery query) {

        StringBuilder sqlQuery =
                new StringBuilder("SELECT tei FROM ")
                        .append(TrackedEntityInstanceTableInfo.TABLE_INFO.name())
                        .append(" tei");

        if (hasPrograms(query)) {
            sqlQuery.append(" JOIN ")
                    .append(EnrollmentTableInfo.TABLE_INFO.name())
                    .append(" en")
                    .append(" ON tei.uid = en.trackedentityinstance");

            WhereClauseBuilder where = new WhereClauseBuilder();
            if (query.program() != null) {
                where.appendKeyStringValue("en.program", query.program());
            }
            if (query.programStartDate() != null) {
                where.appendKeyGreaterOrEqStringValue("en.enrollmentdate", query.formattedProgramStartDate());
            }
            if (query.programEndDate() != null) {
                where.appendKeyLessThanOrEqStringValue("en.enrollmentdate", query.formattedProgramEndDate());
            }
            sqlQuery.append(" WHERE ")
                    .append(where.build());
        }

        if (hasOrgunits(query)) {

            sqlQuery.append(" JOIN ")
                    .append(OrganisationUnitTableInfo.TABLE_INFO.name())
                    .append(" ou")
                    .append(" ON tei.organisationUnit = ou.uid");

            WhereClauseBuilder where = new WhereClauseBuilder();

            if (OuMode.DESCENDANTS.equals(query.orgUnitMode())) {
                for (String orgunit : query.orgUnits()) {
                    where.appendOrKeyLikeStringValue("ou.path", "%" + orgunit + "%");
                }
            } else if (OuMode.CHILDREN.equals(query.orgUnitMode())) {
                for (String orgunit : query.orgUnits()) {
                    where.appendOrKeyStringValue("ou.parent", orgunit);
                    // TODO Include orgunit?
                    where.appendOrKeyStringValue("ou.uid", orgunit);
                }
            } else {
                // By default we use SELECTED
                for (String orgunit : query.orgUnits()) {
                    where.appendOrKeyStringValue("ou.uid", orgunit);
                }
            }

            sqlQuery.append(" WHERE ")
                    .append(where.build());
        }

        return sqlQuery.toString();
    }

    private static boolean hasPrograms(TrackedEntityInstanceQuery query) {
        return query.program() != null ||
                query.programStartDate() != null ||
                query.programEndDate() != null;
    }

    private static boolean hasOrgunits(TrackedEntityInstanceQuery query) {
        // TODO Exclude ALL and ACCESSIBLE
        return !query.orgUnits().isEmpty() ||
                query.orgUnitMode() != null;
    }

}