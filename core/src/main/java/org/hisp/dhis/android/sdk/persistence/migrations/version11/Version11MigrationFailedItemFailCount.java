package org.hisp.dhis.android.sdk.persistence.migrations.version11;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem$Table;

@Migration(version = 11, databaseName = Dhis2Database.NAME)
public class Version11MigrationFailedItemFailCount extends AlterTableMigration<FailedItem> {

    public Version11MigrationFailedItemFailCount(Class<FailedItem> table) {
        super(FailedItem.class);
    }

    public Version11MigrationFailedItemFailCount() {
        super(FailedItem.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(FailedItem.class, FailedItem$Table.FAILCOUNT)) {
            addColumn(Integer.class, FailedItem$Table.FAILCOUNT);
        }
    }
}
