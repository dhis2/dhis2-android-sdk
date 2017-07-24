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
package org.hisp.dhis.android.core.program;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

public class ProgramStageSectionProgramIndicatorLinkStoreImpl implements ProgramStageSectionProgramIndicatorLinkStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            ProgramStageSectionProgramIndicatorLinkModel.TABLE + " (" +
            ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_STAGE_SECTION + ", " +
            ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_INDICATOR + ") " +
            "VALUES(?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " +
            ProgramStageSectionProgramIndicatorLinkModel.TABLE + " SET " +
            ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_STAGE_SECTION + " =?, " +
            ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_INDICATOR + " =? " +
            " WHERE " + ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_STAGE_SECTION + " =? " +
            " AND " + ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_INDICATOR + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;

    public ProgramStageSectionProgramIndicatorLinkStoreImpl(DatabaseAdapter databaseAdapter) {
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
    }

    @Override
    public Long insert(@NonNull String programStageSection, @NonNull String programIndicator) {

        isNull(programStageSection);
        isNull(programIndicator);

        sqLiteBind(insertStatement, 1, programStageSection);
        sqLiteBind(insertStatement, 2, programIndicator);

        Long insert = insertStatement.executeInsert();
        insertStatement.clearBindings();
        return insert;
    }

    @Override
    public int update(@NonNull String programStageSection, @NonNull String programIndicator,
                      @NonNull String whereProgramStageSection, @NonNull String whereProgramIndicator) {
        isNull(programStageSection);
        isNull(programIndicator);
        isNull(whereProgramStageSection);
        isNull(whereProgramIndicator);

        sqLiteBind(updateStatement, 1, programStageSection);
        sqLiteBind(updateStatement, 2, programIndicator);

        // bind the where arguments
        sqLiteBind(updateStatement, 3, whereProgramStageSection);
        sqLiteBind(updateStatement, 4, whereProgramIndicator);

        // execute and clear bindings
        int update = updateStatement.executeUpdateDelete();
        updateStatement.clearBindings();
        return update;
    }
}
