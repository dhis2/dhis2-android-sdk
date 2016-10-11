package org.hisp.dhis.android.sdk.persistence.migrations.version12;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage$Table;

@Migration(version = 12, databaseName = Dhis2Database.NAME)
public class Version12MigrationProgramStageStandardInterval extends AlterTableMigration<ProgramStage> {

    public Version12MigrationProgramStageStandardInterval(Class<ProgramStage> table) {
        super(ProgramStage.class);
    }

    public Version12MigrationProgramStageStandardInterval() {
        super(ProgramStage.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(ProgramStage.class, ProgramStage$Table.STANDARDINTERVAL)) {
            addColumn(Integer.class, ProgramStage$Table.STANDARDINTERVAL);
        }
    }
}
