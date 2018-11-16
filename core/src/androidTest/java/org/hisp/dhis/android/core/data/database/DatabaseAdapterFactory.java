package org.hisp.dhis.android.core.data.database;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.facebook.stetho.Stetho;

public class DatabaseAdapterFactory {
    private static String dbName = null;
    private static DatabaseAdapter databaseAdapter = null;

    private static DatabaseAdapter create() {
        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context, dbName);
        dbOpenHelper.getWritableDatabase();
        Stetho.initializeWithDefaults(context);
        return new SqLiteDatabaseAdapter(dbOpenHelper);
    }

    public static DatabaseAdapter get(boolean foreignKeyConstraintsEnabled) {
        if (databaseAdapter == null) {
            databaseAdapter = create();
        }

        databaseAdapter.database().setForeignKeyConstraintsEnabled(foreignKeyConstraintsEnabled);

        return databaseAdapter;
    }
}