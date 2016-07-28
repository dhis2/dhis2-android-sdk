package org.hisp.dhis.android.sdk.persistence.migrations.version8;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.Program;

@Migration(version = 8, databaseName = Dhis2Database.NAME)
public class Version8MigrationProgramDisplayFrontPageList extends AlterTableMigration<Program> {

    public Version8MigrationProgramDisplayFrontPageList(Class<Program> table) {
        super(Program.class);
    }

    public Version8MigrationProgramDisplayFrontPageList() {
        super(Program.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(Program.class, "displayFrontPageList")) {
            addColumn(Boolean.class, "displayFrontPageList");
        }
    }
}
