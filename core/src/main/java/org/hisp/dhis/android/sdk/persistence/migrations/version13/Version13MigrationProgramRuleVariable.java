package org.hisp.dhis.android.sdk.persistence.migrations.version13;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable$Table;

@Migration(version = 13, databaseName = Dhis2Database.NAME)
public class Version13MigrationProgramRuleVariable extends AlterTableMigration<ProgramRuleVariable> {

    public Version13MigrationProgramRuleVariable(Class<ProgramRuleVariable> table) {
        super(ProgramRuleVariable.class);
    }

    public Version13MigrationProgramRuleVariable() {
        super(ProgramRuleVariable.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(ProgramRuleVariable.class, ProgramRuleVariable$Table.TRACKEDENTITYATTRIBUTE)) {
            addColumn(String.class, ProgramRuleVariable$Table.TRACKEDENTITYATTRIBUTE);
        }
        if (!MigrationUtil.columnExists(ProgramRuleVariable.class, ProgramRuleVariable$Table.PROGRAMSTAGE)) {
            addColumn(String.class, ProgramRuleVariable$Table.PROGRAMSTAGE);
        }
    }
}
