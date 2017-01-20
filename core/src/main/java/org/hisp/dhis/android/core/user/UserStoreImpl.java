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

 package org.hisp.dhis.android.core.user;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class UserStoreImpl implements UserStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + UserModel.TABLE + " (" +
            UserModel.Columns.UID + ", " +
            UserModel.Columns.CODE + ", " +
            UserModel.Columns.NAME + ", " +
            UserModel.Columns.DISPLAY_NAME + ", " +
            UserModel.Columns.CREATED + ", " +
            UserModel.Columns.LAST_UPDATED + ", " +
            UserModel.Columns.BIRTHDAY + ", " +
            UserModel.Columns.EDUCATION + ", " +
            UserModel.Columns.GENDER + ", " +
            UserModel.Columns.JOB_TITLE + ", " +
            UserModel.Columns.SURNAME + ", " +
            UserModel.Columns.FIRST_NAME + ", " +
            UserModel.Columns.INTRODUCTION + ", " +
            UserModel.Columns.EMPLOYER + ", " +
            UserModel.Columns.INTERESTS + ", " +
            UserModel.Columns.LANGUAGES + ", " +
            UserModel.Columns.EMAIL + ", " +
            UserModel.Columns.PHONE_NUMBER + ", " +
            UserModel.Columns.NATIONALITY +
            ") " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final SQLiteDatabase sqLiteDatabase;
    private final SQLiteStatement insertRowStatement;

    public UserStoreImpl(SQLiteDatabase database) {
        this.sqLiteDatabase = database;
        this.insertRowStatement = database.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(
            @NonNull String uid, @Nullable String code,
            @Nullable String name, @Nullable String displayName,
            @Nullable Date created, @Nullable Date lastUpdated,
            @Nullable String birthday, @Nullable String education, @Nullable String gender,
            @Nullable String jobTitle, @Nullable String surname, @Nullable String firstName,
            @Nullable String introduction, @Nullable String employer, @Nullable String interests,
            @Nullable String languages, @Nullable String email, @Nullable String phoneNumber,
            @Nullable String nationality) {
        insertRowStatement.clearBindings();

        sqLiteBind(insertRowStatement, 1, uid);
        sqLiteBind(insertRowStatement, 2, code);
        sqLiteBind(insertRowStatement, 3, name);
        sqLiteBind(insertRowStatement, 4, displayName);
        sqLiteBind(insertRowStatement, 5, created);
        sqLiteBind(insertRowStatement, 6, lastUpdated);
        sqLiteBind(insertRowStatement, 7, birthday);
        sqLiteBind(insertRowStatement, 8, education);
        sqLiteBind(insertRowStatement, 9, gender);
        sqLiteBind(insertRowStatement, 10, jobTitle);
        sqLiteBind(insertRowStatement, 11, surname);
        sqLiteBind(insertRowStatement, 12, firstName);
        sqLiteBind(insertRowStatement, 13, introduction);
        sqLiteBind(insertRowStatement, 14, employer);
        sqLiteBind(insertRowStatement, 15, interests);
        sqLiteBind(insertRowStatement, 16, languages);
        sqLiteBind(insertRowStatement, 17, email);
        sqLiteBind(insertRowStatement, 18, phoneNumber);
        sqLiteBind(insertRowStatement, 19, nationality);

        return insertRowStatement.executeInsert();
    }

    @Override
    public int delete() {
        return sqLiteDatabase.delete(UserModel.TABLE, null, null);
    }

    @Override
    public void close() {
        insertRowStatement.close();
    }
}
