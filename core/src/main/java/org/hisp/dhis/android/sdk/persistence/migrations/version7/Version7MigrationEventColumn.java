package org.hisp.dhis.android.sdk.persistence.migrations.version7;


import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.Event;

@Migration(version = 7, databaseName = Dhis2Database.NAME)
public class Version7MigrationEventColumn extends AlterTableMigration<Event> {

    public Version7MigrationEventColumn(Class<DataElement> table) {
        super(Event.class);
    }

    public Version7MigrationEventColumn() {
        super(Event.class);
    }

    @Override
    public void onPreMigrate() {
        addColumn(String.class, "completedDate");
    }
}
