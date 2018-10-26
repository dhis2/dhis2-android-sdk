package org.hisp.dhis.android.core.data.database;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.facebook.stetho.Stetho;

import org.hisp.dhis.android.core.arch.db.implementations.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.implementations.brite.BriteOpenHelper;
import org.hisp.dhis.android.core.arch.db.implementations.brite.BriteDatabaseAdapter;

public class DatabaseAdapterFactory {
    private static String dbName = null;
    private static DatabaseAdapter databaseAdapter = null;

    private static DatabaseAdapter create() {
        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        BriteOpenHelper dbOpenHelper = new BriteOpenHelper(context, dbName);
        dbOpenHelper.getWritableDatabase();
        Stetho.initializeWithDefaults(context);
        return new BriteDatabaseAdapter(dbOpenHelper);
    }

    public static DatabaseAdapter get(boolean foreignKeyConstraintsEnabled) {
        if (databaseAdapter == null) {
            databaseAdapter = create();
        }

        databaseAdapter.database().setForeignKeyConstraintsEnabled(foreignKeyConstraintsEnabled);

        return databaseAdapter;
    }
}