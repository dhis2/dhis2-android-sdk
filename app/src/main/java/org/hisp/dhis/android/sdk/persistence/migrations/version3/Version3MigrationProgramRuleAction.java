package org.hisp.dhis.android.sdk.persistence.migrations.version3;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction;

/**
 * @author Simen Skogly Russnes on 11.05.15.
 */
@Migration(version = 3, databaseName = Dhis2Database.NAME)
public class Version3MigrationProgramRuleAction extends AlterTableMigration<ProgramRuleAction> {


    public Version3MigrationProgramRuleAction() {
        super(ProgramRuleAction.class);
    }

    @Override
    public void onPreMigrate() {
        // Simple ALTER TABLE migration wraps the statements into a nice builder notation
        super.onPreMigrate();
        addColumn(String.class, "programStageSection");
    }
}
