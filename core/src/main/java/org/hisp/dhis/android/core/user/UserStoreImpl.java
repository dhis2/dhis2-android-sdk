package org.hisp.dhis.android.core.user;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class UserStoreImpl implements UserStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + DbOpenHelper.Tables.USER + " (" +
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
        return sqLiteDatabase.delete(DbOpenHelper.Tables.USER, null, null);
    }

    @Override
    public void close() {
        insertRowStatement.close();
    }
}
