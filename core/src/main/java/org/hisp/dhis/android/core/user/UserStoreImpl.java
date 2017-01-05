package org.hisp.dhis.android.core.user;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.user.UserContract.Columns;

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
            @NonNull String uid, @Nullable String code,
            @NonNull String name, @NonNull String displayName,
            @NonNull Date created, @NonNull Date lastUpdated,
            @Nullable String birthday, @Nullable String education, @Nullable String gender,
            @Nullable String jobTitle, @Nullable String surname, @Nullable String firstName,
            @Nullable String introduction, @Nullable String employer, @Nullable String interests,
            @Nullable String languages, @Nullable String email, @Nullable String phoneNumber,
            @Nullable String nationality) {
        insertRowStatement.clearBindings();

        insertRowStatement.bindString(1, uid);

        if (code != null) {
            insertRowStatement.bindString(2, code);
        } else {
            insertRowStatement.bindNull(2);
        }
        insertRowStatement.bindString(3, name);
        insertRowStatement.bindString(4, displayName);
        insertRowStatement.bindString(5, BaseIdentifiableObject.DATE_FORMAT.format(created));
        insertRowStatement.bindString(6, BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));

        if (birthday != null) {
            insertRowStatement.bindString(7, birthday);
        } else {
            insertRowStatement.bindNull(7);
        }

        if (education != null) {
            insertRowStatement.bindString(8, education);
        } else {
            insertRowStatement.bindNull(8);
        }

        if (gender != null) {
            insertRowStatement.bindString(9, gender);
        } else {
            insertRowStatement.bindNull(9);
        }

        if (jobTitle != null) {
            insertRowStatement.bindString(10, jobTitle);
        } else {
            insertRowStatement.bindNull(10);
        }

        if (surname != null) {
            insertRowStatement.bindString(11, surname);
        } else {
            insertRowStatement.bindNull(11);
        }

        if (firstName != null) {
            insertRowStatement.bindString(12, firstName);
        } else {
            insertRowStatement.bindNull(12);
        }

        if (introduction != null) {
            insertRowStatement.bindString(13, introduction);
        } else {
            insertRowStatement.bindNull(13);
        }

        if (employer != null) {
            insertRowStatement.bindString(14, employer);
        } else {
            insertRowStatement.bindNull(14);
        }

        if (interests != null) {
            insertRowStatement.bindString(15, interests);
        } else {
            insertRowStatement.bindNull(15);
        }

        if (languages != null) {
            insertRowStatement.bindString(16, languages);
        } else {
            insertRowStatement.bindNull(16);
        }

        if (email != null) {
            insertRowStatement.bindString(17, email);
        } else {
            insertRowStatement.bindNull(17);
        }

        if (phoneNumber != null) {
            insertRowStatement.bindString(18, phoneNumber);
        } else {
            insertRowStatement.bindNull(18);
        }

        if (nationality != null) {
            insertRowStatement.bindString(19, nationality);
        } else {
            insertRowStatement.bindNull(19);
        }

        return insertRowStatement.executeInsert();
    }

    @Override
    public void close() {
        insertRowStatement.close();
    }
}
