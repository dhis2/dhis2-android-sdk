package org.hisp.dhis.android.sdk.persistence.migrations.version14;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.migration.UpdateTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.Event$Table;

@Migration(version = 15, databaseName = Dhis2Database.NAME)
public class Version14MigrationSetEventDataToUnsent extends UpdateTableMigration<Event> {

    /**
     * Creates an update migration.
     *
     * @param table The table to update
     */
    public Version14MigrationSetEventDataToUnsent(Class<Event> table) {
        super(Event.class);
    }

    public Version14MigrationSetEventDataToUnsent() {
        super(Event.class);
    }

    @Override
    public void onPreMigrate() {
//        set(Condition.column(Event$Table.FROMSERVER).eq(0)).where(Condition.column(Event$Table.FROMSERVER).eq(1));
    }

}
