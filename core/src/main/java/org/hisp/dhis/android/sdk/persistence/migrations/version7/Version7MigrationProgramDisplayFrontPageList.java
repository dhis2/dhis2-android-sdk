package org.hisp.dhis.android.sdk.persistence.migrations.version7;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.Program;

@Migration(version = 7, databaseName = Dhis2Database.NAME)
public class Version7MigrationProgramDisplayFrontPageList  extends AlterTableMigration<Program> {

    public Version7MigrationProgramDisplayFrontPageList(Class<Program> table) {
        super(Program.class);
    }

    public Version7MigrationProgramDisplayFrontPageList() {
        super(Program.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        addColumn(Boolean.class, "displayFrontPageList");
    }
}
