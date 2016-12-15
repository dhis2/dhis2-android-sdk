package org.hisp.dhis.android.core.data.database;

import android.database.sqlite.SQLiteDatabase;

import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.junit.After;
import org.junit.Before;

public abstract class AbsStoreTestCase {
    private SQLiteDatabase sqLiteDatabase;
    private UserCredentialsStore userCredentialsStore;

    @Before
    public void setUp() {
        sqLiteDatabase = SQLiteDatabase.create(null);
        sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON;");
        DbOpenHelper.create(sqLiteDatabase);
    }

    @After
    public void tearDown() {
        sqLiteDatabase.close();
    }

    protected SQLiteDatabase database() {
        return sqLiteDatabase;
    }
}
