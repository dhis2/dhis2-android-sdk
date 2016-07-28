package org.hisp.dhis.android.sdk.persistence.migrations.version8;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage;

@Migration(version = 8, databaseName = Dhis2Database.NAME)
public class Version8MigrationProgramStageExecutionLabel extends AlterTableMigration<ProgramStage> {

    public Version8MigrationProgramStageExecutionLabel(Class<ProgramStage> table) {
        super(ProgramStage.class);
    }

    public Version8MigrationProgramStageExecutionLabel() {
        super(ProgramStage.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(ProgramStage.class, "executionDateLabel")) {
            addColumn(String.class, "executionDateLabel");
        }
    }
}
