package org.hisp.dhis.android.core.wipe;

import org.hisp.dhis.android.core.arch.db.TableInfo;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class TableWiper {

    private final DatabaseAdapter databaseAdapter;

    @Inject
    public TableWiper(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    public void wipeTable(String tableName) {
        databaseAdapter.delete(tableName);
    }

    public void wipeTables(String ...tableNames) {
        for (String tableName: tableNames) {
            wipeTable(tableName);
        }
    }

    public void wipeTable(TableInfo tableInfo) {
        wipeTable(tableInfo.name());
    }

    public void wipeTables(TableInfo ...tableInfos) {
        for (TableInfo tableInfo: tableInfos) {
            wipeTable(tableInfo);
        }
    }
}