/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.utils.services;

import android.database.Cursor;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkModel;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;

public final class D2Synchronizer {

    public static void syncReservedValues() {
        String selectStatement = generateSelectStatement();
        Cursor cursor = D2.d2.databaseAdapter().query(selectStatement);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String ownerUid = cursor.getString(0);
                String orgUnitUid = cursor.getString(1);
                D2.d2.syncTrackedEntityAttributeReservedValue(ownerUid, orgUnitUid);
            } while (cursor.moveToNext());
        }
    }

    private static String generateSelectStatement() {
        String TEAUidColumn = "t." + TrackedEntityAttributeModel.Columns.UID;
        String TEAGeneratedColumn = "t." + TrackedEntityAttributeModel.Columns.GENERATED;
        String OUPLOrgUnitColumn = "o." + OrganisationUnitProgramLinkModel.Columns.ORGANISATION_UNIT ;
        String OUPLProgramColumn = "o." + OrganisationUnitProgramLinkModel.Columns.PROGRAM;
        String PTEATEAColumn = "p." + ProgramTrackedEntityAttributeModel.Columns.TRACKED_ENTITY_ATTRIBUTE;
        String PTEAProgramColumn = "p." + ProgramTrackedEntityAttributeModel.Columns.PROGRAM;

        return "SELECT DISTINCT " +
                TEAUidColumn + ", " +
                OUPLOrgUnitColumn  + " " +

                "FROM " +
                TrackedEntityAttributeModel.TABLE + " t, " +
                OrganisationUnitProgramLinkModel.TABLE + " o, " +
                ProgramTrackedEntityAttributeModel.TABLE + " p " +

                "WHERE " +
                TEAGeneratedColumn + " = 1 AND " +
                PTEATEAColumn + " = " + TEAUidColumn + " AND " +
                PTEAProgramColumn + " = " + OUPLProgramColumn + ";";
    }
}