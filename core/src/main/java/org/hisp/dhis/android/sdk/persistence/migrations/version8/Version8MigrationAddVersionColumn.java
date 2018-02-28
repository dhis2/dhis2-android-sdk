package org.hisp.dhis.android.sdk.persistence.migrations.version8;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;

@Migration(version = 8, databaseName = Dhis2Database.NAME)
public class Version8MigrationAddVersionColumn extends
        AlterTableMigration<SystemInfo> {

    public Version8MigrationAddVersionColumn(Class<SystemInfo> table) {
        super(SystemInfo.class);
    }

    public Version8MigrationAddVersionColumn() {
        super(SystemInfo.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        addColumn(String.class, "version");
    }
}