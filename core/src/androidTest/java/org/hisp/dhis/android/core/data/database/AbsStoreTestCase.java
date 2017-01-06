package org.hisp.dhis.android.core.data.database;

import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public abstract class AbsStoreTestCase {
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() throws IOException {
        sqLiteDatabase = DbOpenHelper.create();
        sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON;");
    }

    @After
    public void tearDown() throws IOException {
        assertThat(sqLiteDatabase).isNotNull();
        sqLiteDatabase.close();
    }

    protected SQLiteDatabase database() {
        return sqLiteDatabase;
    }
}
