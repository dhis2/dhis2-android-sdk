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
package org.hisp.dhis.android.core.organisationunit;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public class OrganisationUnitProgramLinkStoreImpl implements OrganisationUnitProgramLinkStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            OrganisationUnitProgramLinkModel.TABLE + " (" +
            OrganisationUnitProgramLinkModel.Columns.ORGANISATION_UNIT + ", " +
            OrganisationUnitProgramLinkModel.Columns.PROGRAM + ") " +
            "VALUES(?,?);";

    private final SQLiteStatement insertStatement;
    private final DatabaseAdapter databaseAdapter;

    public OrganisationUnitProgramLinkStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String organisationUnitUid, @NonNull String programUid) {

        isNull(organisationUnitUid);
        isNull(programUid);
        sqLiteBind(insertStatement, 1, organisationUnitUid);
        sqLiteBind(insertStatement, 2, programUid);

        long ret = databaseAdapter.executeInsert(OrganisationUnitProgramLinkModel.TABLE,
                insertStatement);
        insertStatement.clearBindings();
        return ret;
    }

    @Override
    public boolean exists(@NonNull String organisationUnitUid, @NonNull String programUid) {
        String select = String.format(
                "SELECT * FROM " + OrganisationUnitProgramLinkModel.TABLE + " WHERE " +
                        OrganisationUnitProgramLinkModel.Columns.ORGANISATION_UNIT + " = '%s' AND "
                        +
                        OrganisationUnitProgramLinkModel.Columns.PROGRAM + " = '%s' ",
                organisationUnitUid, programUid);

        Cursor cursor = null;
        int count = 0;

        try {
            cursor = databaseAdapter.query(select, null);

            count = cursor.getCount();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count > 0;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(OrganisationUnitProgramLinkModel.TABLE);
    }
}
