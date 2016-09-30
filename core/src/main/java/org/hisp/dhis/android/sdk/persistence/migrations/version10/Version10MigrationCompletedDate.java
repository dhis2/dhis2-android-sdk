package org.hisp.dhis.android.sdk.persistence.migrations.version10;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.Event;

@Migration(version = 10, databaseName = Dhis2Database.NAME)
public class Version10MigrationCompletedDate extends AlterTableMigration<Event> {

    public Version10MigrationCompletedDate(Class<Event> table) {
        super(Event.class);
    }

    public Version10MigrationCompletedDate() {
        super(Event.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(Event.class, "completedDate")) {
            addColumn(String.class, "completedDate");
        }
    }
}