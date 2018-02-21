package org.hisp.dhis.android.sdk.persistence.migrations.version18;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo$Table;


@Migration(version = 18, databaseName = Dhis2Database.NAME)
public class Version18MigrationAddSystemInfoColumn extends
        AlterTableMigration<SystemInfo> {

    public Version18MigrationAddSystemInfoColumn(Class<SystemInfo> table) {
        super(SystemInfo.class);
    }

    public Version18MigrationAddSystemInfoColumn() {
        super(SystemInfo.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(SystemInfo.class,
                SystemInfo$Table.VERSION)) {
            addColumn(Boolean.class, SystemInfo$Table.VERSION);
        }
    }
}
