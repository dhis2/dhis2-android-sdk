package org.hisp.dhis.android.core.data.database.migrations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;



public class DbOpenHelperMigrationTester extends DbOpenHelper {
    static final int startVersion = 1;

    public DbOpenHelperMigrationTester(
            @NonNull Context context,
            @Nullable String databaseName, int version) {
        super(context, databaseName, version);
    }

    public DbOpenHelperMigrationTester(
            @NonNull Context context,
            @Nullable String databaseName) {
        super(context, databaseName, startVersion);
    }
}