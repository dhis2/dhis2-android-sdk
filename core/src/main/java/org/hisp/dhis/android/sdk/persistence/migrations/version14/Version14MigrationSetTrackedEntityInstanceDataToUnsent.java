package org.hisp.dhis.android.sdk.persistence.migrations.version14;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.migration.UpdateTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance$Table;

@Migration(version = 15, databaseName = Dhis2Database.NAME)
public class Version14MigrationSetTrackedEntityInstanceDataToUnsent extends UpdateTableMigration<TrackedEntityInstance> {

    public Version14MigrationSetTrackedEntityInstanceDataToUnsent() {
        super(TrackedEntityInstance.class);
    }

    @Override
    public void onPreMigrate() {
//        set(Condition.column(TrackedEntityInstance$Table.FROMSERVER).eq(0)).where(Condition.column(TrackedEntityInstance$Table.FROMSERVER).eq(1));
    }
}
