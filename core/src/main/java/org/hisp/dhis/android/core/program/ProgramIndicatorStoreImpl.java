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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class ProgramIndicatorStoreImpl implements ProgramIndicatorStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + ProgramIndicatorModel.PROGRAM_INDICATOR + " (" +
            ProgramIndicatorModel.Columns.UID + ", " +
            ProgramIndicatorModel.Columns.CODE + ", " +
            ProgramIndicatorModel.Columns.NAME + ", " +
            ProgramIndicatorModel.Columns.DISPLAY_NAME + ", " +
            ProgramIndicatorModel.Columns.CREATED + ", " +
            ProgramIndicatorModel.Columns.LAST_UPDATED + ", " +
            ProgramIndicatorModel.Columns.SHORT_NAME + ", " +
            ProgramIndicatorModel.Columns.DISPLAY_SHORT_NAME + ", " +
            ProgramIndicatorModel.Columns.DESCRIPTION + ", " +
            ProgramIndicatorModel.Columns.DISPLAY_DESCRIPTION + ", " +
            ProgramIndicatorModel.Columns.DISPLAY_IN_FORM + ", " +
            ProgramIndicatorModel.Columns.EXPRESSION + ", " +
            ProgramIndicatorModel.Columns.DIMENSION_ITEM + ", " +
            ProgramIndicatorModel.Columns.FILTER + ", " +
            ProgramIndicatorModel.Columns.DECIMALS +
            ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private final SQLiteStatement insertRowStatement;

    public ProgramIndicatorStoreImpl(SQLiteDatabase database) {
        this.insertRowStatement = database.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
                       @Nullable String displayName, @NonNull Date created,
                       @NonNull Date lastUpdated, @Nullable String shortName,
                       @Nullable String displayShortName, @Nullable String description,
                       @Nullable String displayDescription, @Nullable Boolean displayInForm,
                       @Nullable String expression, @Nullable String dimensionItem,
                       @Nullable String filter, @Nullable Integer decimals) {
        insertRowStatement.clearBindings();

        sqLiteBind(insertRowStatement, 1, uid);
        sqLiteBind(insertRowStatement, 2, code);
        sqLiteBind(insertRowStatement, 3, name);
        sqLiteBind(insertRowStatement, 4, displayName);
        sqLiteBind(insertRowStatement, 5, created);
        sqLiteBind(insertRowStatement, 6, lastUpdated);
        sqLiteBind(insertRowStatement, 7, shortName);
        sqLiteBind(insertRowStatement, 8, displayShortName);
        sqLiteBind(insertRowStatement, 9, description);
        sqLiteBind(insertRowStatement, 10, displayDescription);
        sqLiteBind(insertRowStatement, 11, displayInForm);
        sqLiteBind(insertRowStatement, 12, expression);
        sqLiteBind(insertRowStatement, 13, dimensionItem);
        sqLiteBind(insertRowStatement, 14, filter);
        sqLiteBind(insertRowStatement, 15, decimals);

        return insertRowStatement.executeInsert();
    }

    @Override
    public void close() {
        insertRowStatement.close();
    }
}
