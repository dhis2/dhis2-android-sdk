package org.hisp.dhis.android.core.database;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.test.ProviderTestCase2;

public abstract class AbsProviderTestCase extends ProviderTestCase2<DbContentProvider> {
    private SQLiteDatabase writableDatabase;

    protected AbsProviderTestCase() {
        super(DbContentProvider.class, DbContract.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());

        super.setUp();

        // using database directly to verify behaviour of content provider
        writableDatabase = getProvider().sqLiteOpenHelper().getWritableDatabase();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        // close database in order not to leak resources during tests
        writableDatabase.close();
    }

    protected SQLiteDatabase database() {
        return writableDatabase;
    }
}
