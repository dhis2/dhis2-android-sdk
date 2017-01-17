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

import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class ProgramRuleVariableModelStoreImpl implements ProgramRuleVariableModelStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.PROGRAM_RULE_VARIABLE + " (" +
            ProgramRuleVariableModel.Columns.UID + ", " +
            ProgramRuleVariableModel.Columns.CODE + ", " +
            ProgramRuleVariableModel.Columns.NAME + ", " +
            ProgramRuleVariableModel.Columns.DISPLAY_NAME + ", " +
            ProgramRuleVariableModel.Columns.CREATED + ", " +
            ProgramRuleVariableModel.Columns.LAST_UPDATED + ", " +
            ProgramRuleVariableModel.Columns.USE_CODE_FOR_OPTION_SET + ", " +
            ProgramRuleVariableModel.Columns.PROGRAM + ", " +
            ProgramRuleVariableModel.Columns.PROGRAM_STAGE + ", " +
            ProgramRuleVariableModel.Columns.DATA_ELEMENT + ", " +
            ProgramRuleVariableModel.Columns.TRACKED_ENTITY_ATTRIBUTE + ", " +
            ProgramRuleVariableModel.Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE + ") " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public ProgramRuleVariableModelStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
                       @NonNull String displayName, @NonNull Date created,
                       @NonNull Date lastUpdated, @Nullable Boolean useCodeForOptionSet,
                       @NonNull String program, @Nullable String programStage,
                       @Nullable String dataElement, @Nullable String trackedEntityAttribute,
                       @Nullable ProgramRuleVariableSourceType programRuleVariableSourceType) {
        sqLiteStatement.clearBindings();

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, useCodeForOptionSet);
        sqLiteBind(sqLiteStatement, 8, program);
        sqLiteBind(sqLiteStatement, 9, programStage);
        sqLiteBind(sqLiteStatement, 10, dataElement);
        sqLiteBind(sqLiteStatement, 11, trackedEntityAttribute);
        sqLiteBind(sqLiteStatement, 12, programRuleVariableSourceType.name());

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
