package org.hisp.dhis.android.sdk.persistence.migrations.version13;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction$Table;

@Migration(version = 13, databaseName = Dhis2Database.NAME)
public class Version13MigrationProgramRuleAction extends AlterTableMigration<ProgramRuleAction> {

    public Version13MigrationProgramRuleAction(Class<ProgramRuleAction> table) {
        super(ProgramRuleAction.class);
    }

    public Version13MigrationProgramRuleAction() {
        super(ProgramRuleAction.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(ProgramRuleAction.class, ProgramRuleAction$Table.TRACKEDENTITYATTRIBUTE)) {
            addColumn(String.class, ProgramRuleAction$Table.TRACKEDENTITYATTRIBUTE);
        }
        if (!MigrationUtil.columnExists(ProgramRuleAction.class, ProgramRuleAction$Table.LOCATION)) {
            addColumn(String.class, ProgramRuleAction$Table.LOCATION);
        }
        if (!MigrationUtil.columnExists(ProgramRuleAction.class, ProgramRuleAction$Table.CONTENT)) {
            addColumn(String.class, ProgramRuleAction$Table.CONTENT);
        }
        if (!MigrationUtil.columnExists(ProgramRuleAction.class, ProgramRuleAction$Table.DATA)) {
            addColumn(String.class, ProgramRuleAction$Table.DATA);
        }
    }
}
