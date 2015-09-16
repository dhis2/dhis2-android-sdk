package org.hisp.dhis.android.sdk.persistence.migrations.version5;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute;

/**
 * Created by erling on 9/8/15.
 */
@Migration(version = 5, databaseName = Dhis2Database.NAME)
public class Version5MigrationProgramTrackedEntityAttribute extends AlterTableMigration<ProgramTrackedEntityAttribute> {


    public Version5MigrationProgramTrackedEntityAttribute() {
        super(ProgramTrackedEntityAttribute.class);
    }

    @Override
    public void onPreMigrate() {
        // Simple ALTER TABLE migration wraps the statements into a nice builder notation
        super.onPreMigrate();
        addColumn(int.class, "sortOrder");
    }
}
