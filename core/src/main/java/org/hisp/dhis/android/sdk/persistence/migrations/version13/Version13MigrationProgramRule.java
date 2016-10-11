package org.hisp.dhis.android.sdk.persistence.migrations.version13;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule$Table;

@Migration(version = 13, databaseName = Dhis2Database.NAME)
public class Version13MigrationProgramRule extends AlterTableMigration<ProgramRule> {

    public Version13MigrationProgramRule(Class<ProgramRule> table) {
        super(ProgramRule.class);
    }

    public Version13MigrationProgramRule() {
        super(ProgramRule.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(ProgramRule.class, ProgramRule$Table.PRIORITY)) {
            addColumn(Integer.class, ProgramRule$Table.PRIORITY);
        }
    }
}
