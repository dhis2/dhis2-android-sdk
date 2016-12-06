package org.hisp.dhis.android.core.commons.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract;
import org.hisp.dhis.android.core.user.UserContract;
import org.hisp.dhis.android.core.user.UserCredentialsContract;
import org.hisp.dhis.android.core.user.UserOrganisationUnitContract;

public final class DbHelper extends SQLiteOpenHelper {
    private static final String NAME = "dhis.db";
    private static final int VERSION = 1;

    public DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // enable foreign key support in database
        db.execSQL("PRAGMA foreign_keys = ON;");

        // create tables if they don't exist yet
        db.execSQL(UserContract.CREATE_TABLE);
        db.execSQL(UserCredentialsContract.CREATE_TABLE);
        db.execSQL(OrganisationUnitContract.CREATE_TABLE);
        db.execSQL(UserOrganisationUnitContract.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ToDo: replace this logic with proper migration mechanism
        db.execSQL(UserContract.DROP_TABLE);
        db.execSQL(UserCredentialsContract.DROP_TABLE);
        db.execSQL(OrganisationUnitContract.DROP_TABLE);
        db.execSQL(UserOrganisationUnitContract.DROP_TABLE);
    }
}
