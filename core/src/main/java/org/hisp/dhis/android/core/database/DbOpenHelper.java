package org.hisp.dhis.android.core.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract;
import org.hisp.dhis.android.core.user.UserContract;
import org.hisp.dhis.android.core.user.UserCredentialsContract;
import org.hisp.dhis.android.core.user.UserOrganisationUnitContract;

public final class DbOpenHelper extends SQLiteOpenHelper {
    private static final String NAME = "dhis.db";
    private static final int VERSION = 1;

    public interface Tables {
        String USER = "User";
        String USER_CREDENTIALS = "UserCredentials";
        String ORGANISATION_UNIT = "OrganisationUnit";
        String USER_ORGANISATION_UNIT = "UserOrganisationUnit";
    }

    private static final String CREATE_USER_TABLE = "CREATE TABLE " + Tables.USER + " (" +
            UserContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            UserContract.Columns.CODE + " TEXT," +
            UserContract.Columns.NAME + " TEXT," +
            UserContract.Columns.DISPLAY_NAME + " TEXT," +
            UserContract.Columns.CREATED + " TEXT," +
            UserContract.Columns.LAST_UPDATED + " TEXT," +
            UserContract.Columns.BIRTHDAY + " TEXT," +
            UserContract.Columns.EDUCATION + " TEXT," +
            UserContract.Columns.GENDER + " TEXT," +
            UserContract.Columns.JOB_TITLE + " TEXT," +
            UserContract.Columns.SURNAME + " TEXT," +
            UserContract.Columns.FIRST_NAME + " TEXT," +
            UserContract.Columns.INTRODUCTION + " TEXT," +
            UserContract.Columns.EMPLOYER + " TEXT," +
            UserContract.Columns.INTERESTS + " TEXT," +
            UserContract.Columns.LANGUAGES + " TEXT," +
            UserContract.Columns.EMAIL + " TEXT," +
            UserContract.Columns.PHONE_NUMBER + " TEXT," +
            UserContract.Columns.NATIONALITY + " TEXT" +
            ");";

    private static final String CREATE_USER_CREDENTIALS_TABLE = "CREATE TABLE " + Tables.USER_CREDENTIALS + " (" +
            UserCredentialsContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserCredentialsContract.Columns.UID + " TEXT," +
            UserCredentialsContract.Columns.CODE + " TEXT," +
            UserCredentialsContract.Columns.NAME + " TEXT," +
            UserCredentialsContract.Columns.DISPLAY_NAME + " TEXT," +
            UserCredentialsContract.Columns.CREATED + " TEXT," +
            UserCredentialsContract.Columns.LAST_UPDATED + " TEXT," +
            UserCredentialsContract.Columns.USERNAME + " TEXT," +
            UserCredentialsContract.Columns.USER + " TEXT NOT NULL UNIQUE," +
            "FOREIGN KEY (" + UserCredentialsContract.Columns.USER + ") REFERENCES " + Tables.USER +
            " (" + UserContract.Columns.UID + ") ON DELETE CASCADE" +
            ");";

    private static final String CREATE_ORGANISATION_UNITS_TABLE = "CREATE TABLE " + Tables.ORGANISATION_UNIT + " (" +
            OrganisationUnitContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            OrganisationUnitContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            OrganisationUnitContract.Columns.CODE + " TEXT," +
            OrganisationUnitContract.Columns.NAME + " TEXT," +
            OrganisationUnitContract.Columns.DISPLAY_NAME + " TEXT," +
            OrganisationUnitContract.Columns.CREATED + " TEXT," +
            OrganisationUnitContract.Columns.LAST_UPDATED + " TEXT," +
            OrganisationUnitContract.Columns.SHORT_NAME + " TEXT," +
            OrganisationUnitContract.Columns.DISPLAY_SHORT_NAME + " TEXT," +
            OrganisationUnitContract.Columns.DESCRIPTION + " TEXT," +
            OrganisationUnitContract.Columns.DISPLAY_DESCRIPTION + " TEXT," +
            OrganisationUnitContract.Columns.PATH + " TEXT," +
            OrganisationUnitContract.Columns.OPENING_DATE + " TEXT," +
            OrganisationUnitContract.Columns.CLOSED_DATE + " TEXT," +
            OrganisationUnitContract.Columns.LEVEL + " INTEGER," +
            OrganisationUnitContract.Columns.PARENT + " TEXT NOT NULL," +
            "FOREIGN KEY (" + OrganisationUnitContract.Columns.PARENT + ") REFERENCES " +
            Tables.ORGANISATION_UNIT + " (" + OrganisationUnitContract.Columns.UID + ") ON DELETE CASCADE" +
            ");";

    private static final String CREATE_USER_ORGANISATION_UNIT_TABLE = "CREATE TABLE " + Tables.USER_ORGANISATION_UNIT + " (" +
            UserOrganisationUnitContract.Columns.USER + " TEXT NOT NULL," +
            UserOrganisationUnitContract.Columns.ORGANISATION_UNIT + " TEXT NOT NULL," +
            "FOREIGN KEY (" + UserOrganisationUnitContract.Columns.USER + ") REFERENCES " + Tables.USER +
            " (" + UserContract.Columns.UID + ") ON DELETE CASCADE," +
            "FOREIGN KEY (" + UserOrganisationUnitContract.Columns.ORGANISATION_UNIT + ") REFERENCES " + Tables.ORGANISATION_UNIT +
            " (" + OrganisationUnitContract.Columns.UID + ") ON DELETE CASCADE," +
            "PRIMARY KEY (" + UserOrganisationUnitContract.Columns.USER + ", " + UserOrganisationUnitContract.Columns.ORGANISATION_UNIT + ")" +
            ");";

    public DbOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_USER_CREDENTIALS_TABLE);
        db.execSQL(CREATE_ORGANISATION_UNITS_TABLE);
        db.execSQL(CREATE_USER_ORGANISATION_UNIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ToDo: logic for proper schema migration
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        // enable foreign key support in database
        db.execSQL("PRAGMA foreign_keys = ON;");
    }
}
