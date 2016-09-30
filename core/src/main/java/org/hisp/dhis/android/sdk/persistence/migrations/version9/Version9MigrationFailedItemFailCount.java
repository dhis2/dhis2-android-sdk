package org.hisp.dhis.android.sdk.persistence.migrations.version9;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem$Table;

@Migration(version = 9, databaseName = Dhis2Database.NAME)
public class Version9MigrationFailedItemFailCount extends AlterTableMigration<FailedItem> {

    public Version9MigrationFailedItemFailCount(Class<FailedItem> table) {
        super(FailedItem.class);
    }

    public Version9MigrationFailedItemFailCount() {
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
