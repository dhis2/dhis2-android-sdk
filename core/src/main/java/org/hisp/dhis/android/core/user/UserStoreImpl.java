package org.hisp.dhis.android.core.user;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.user.UserContract.Columns;
import org.hisp.dhis.android.models.common.BaseIdentifiableObject;

import java.util.Date;

public class UserStoreImpl implements UserStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + DbOpenHelper.Tables.USER + " (" +
            Columns.UID + ", " +
            Columns.CODE + ", " +
            Columns.NAME + ", " +
            Columns.DISPLAY_NAME + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.BIRTHDAY + ", " +
            Columns.EDUCATION + ", " +
            Columns.GENDER + ", " +
            Columns.JOB_TITLE + ", " +
            Columns.SURNAME + ", " +
            Columns.FIRST_NAME + ", " +
            Columns.INTRODUCTION + ", " +
            Columns.EMPLOYER + ", " +
            Columns.INTERESTS + ", " +
            Columns.LANGUAGES + ", " +
            Columns.EMAIL + ", " +
            Columns.PHONE_NUMBER + ", " +
            Columns.NATIONALITY +
            ") " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final SQLiteStatement insertRowStatement;

    public UserStoreImpl(SQLiteDatabase database) {
        this.insertRowStatement = database.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(
            @NonNull String uid, @NonNull String code, @NonNull String name,
            @NonNull String displayName, @NonNull Date created, @NonNull Date lastUpdated,
            @NonNull String birthday, @NonNull String education, @NonNull String gender,
            @NonNull String jobTitle, @NonNull String surname, @NonNull String firstName,
            @NonNull String introduction, @NonNull String employer, @NonNull String interests,
            @NonNull String languages, @NonNull String email, @NonNull String phoneNumber,
            @NonNull String nationality) {
        insertRowStatement.clearBindings();

        insertRowStatement.bindString(1, uid);
        insertRowStatement.bindString(2, code);
        insertRowStatement.bindString(3, name);
        insertRowStatement.bindString(4, displayName);
        insertRowStatement.bindString(5, BaseIdentifiableObject.DATE_FORMAT.format(created));
        insertRowStatement.bindString(6, BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));
        insertRowStatement.bindString(7, birthday);
        insertRowStatement.bindString(8, education);
        insertRowStatement.bindString(9, gender);
        insertRowStatement.bindString(10, jobTitle);
        insertRowStatement.bindString(11, surname);
        insertRowStatement.bindString(12, firstName);
        insertRowStatement.bindString(13, introduction);
        insertRowStatement.bindString(14, employer);
        insertRowStatement.bindString(15, interests);
        insertRowStatement.bindString(16, languages);
        insertRowStatement.bindString(17, email);
        insertRowStatement.bindString(18, phoneNumber);
        insertRowStatement.bindString(19, nationality);

        return insertRowStatement.executeInsert();
    }

    @Override
    public void close() {
        insertRowStatement.close();
    }
}
