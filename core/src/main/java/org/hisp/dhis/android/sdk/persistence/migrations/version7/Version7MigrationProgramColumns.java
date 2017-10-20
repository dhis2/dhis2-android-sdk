package org.hisp.dhis.android.sdk.persistence.migrations.version7;


import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.Program;

@Migration(version = 7, databaseName = Dhis2Database.NAME)
public class Version7MigrationProgramColumns extends AlterTableMigration<Program> {

    public Version7MigrationProgramColumns(Class<DataElement> table) {
        super(Program.class);
    }

    public Version7MigrationProgramColumns() {
        super(Program.class);
    }

    @Override
    public void onPreMigrate() {
        addColumn(Integer.class, "completeEventsExpiryDays");
        addColumn(Integer.class, "expiryDays");
    }
}
