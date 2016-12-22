package org.hisp.dhis.android.core.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.hisp.dhis.android.core.option.OptionSetContract;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract;
import org.hisp.dhis.android.core.user.AuthenticatedUserContract;
import org.hisp.dhis.android.core.user.UserContract;
import org.hisp.dhis.android.core.user.UserCredentialsContract;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkContract;

public final class DbOpenHelper extends SQLiteOpenHelper {

    // @VisibleForTesting
    // static final String NAME = "dhis.db";

    @VisibleForTesting
    static final int VERSION = 1;

    public interface Tables {
        String USER = "User";
        String USER_CREDENTIALS = "UserCredentials";
        String ORGANISATION_UNIT = "OrganisationUnit";
        String USER_ORGANISATION_UNIT = "UserOrganisationUnit";
        String AUTHENTICATED_USER = "AuthenticatedUser";
        String OPTION_SET = "OptionSet";
        String OPTION = "Option";
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
            UserCredentialsContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
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
            OrganisationUnitContract.Columns.PARENT + " TEXT" + ");";

    private static final String CREATE_USER_ORGANISATION_UNIT_TABLE = "CREATE TABLE " + Tables.USER_ORGANISATION_UNIT + " (" +
            UserOrganisationUnitLinkContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserOrganisationUnitLinkContract.Columns.USER + " TEXT NOT NULL," +
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT + " TEXT NOT NULL," +
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT_SCOPE + " TEXT NOT NULL," +
            "FOREIGN KEY (" + UserOrganisationUnitLinkContract.Columns.USER + ") REFERENCES " + Tables.USER +
            " (" + UserContract.Columns.UID + ") ON DELETE CASCADE," +
            "FOREIGN KEY (" + UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT + ") REFERENCES " + Tables.ORGANISATION_UNIT +
            " (" + OrganisationUnitContract.Columns.UID + ") ON DELETE CASCADE," +
            "UNIQUE (" + UserOrganisationUnitLinkContract.Columns.USER + ", " +
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT + ", " +
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT_SCOPE + ")" +
            ");";

    private static final String CREATE_AUTHENTICATED_USER_TABLE = "CREATE TABLE " + Tables.AUTHENTICATED_USER + " (" +
            AuthenticatedUserContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            AuthenticatedUserContract.Columns.USER + " TEXT NOT NULL UNIQUE," +
            AuthenticatedUserContract.Columns.CREDENTIALS + " TEXT NOT NULL," +
            "FOREIGN KEY (" + AuthenticatedUserContract.Columns.USER + ") REFERENCES " + Tables.USER +
            " (" + UserContract.Columns.UID + ") ON DELETE CASCADE" +
            ");";

    private static final String CREATE_OPTION_SET_TABLE = "CREATE TABLE " + Tables.OPTION_SET + " (" +
            OptionSetContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            OptionSetContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            OptionSetContract.Columns.CODE + " TEXT," +
            OptionSetContract.Columns.NAME + " TEXT," +
            OptionSetContract.Columns.DISPLAY_NAME + " TEXT," +
            OptionSetContract.Columns.CREATED + " TEXT," +
            OptionSetContract.Columns.LAST_UPDATED + " TEXT," +
            OptionSetContract.Columns.VERSION + " INTEGER," +
            OptionSetContract.Columns.VALUE_TYPE + " TEXT" +
            ");";

    /**
     * This method should be used only for testing purposes
     */
    // ToDo: Revise usage of this method
    @VisibleForTesting
    static SQLiteDatabase create() {
        return create(SQLiteDatabase.create(null));
    }

    private static SQLiteDatabase create(SQLiteDatabase database) {
        database.execSQL(CREATE_USER_TABLE);
        database.execSQL(CREATE_USER_CREDENTIALS_TABLE);
        database.execSQL(CREATE_ORGANISATION_UNITS_TABLE);
        database.execSQL(CREATE_USER_ORGANISATION_UNIT_TABLE);
        database.execSQL(CREATE_AUTHENTICATED_USER_TABLE);
        database.execSQL(CREATE_OPTION_SET_TABLE);
        return database;
    }

    public DbOpenHelper(@NonNull Context context, @NonNull String databaseName) {
        super(context, databaseName, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        create(db);
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
