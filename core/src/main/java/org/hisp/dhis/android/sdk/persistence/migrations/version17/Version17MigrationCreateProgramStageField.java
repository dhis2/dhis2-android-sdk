package org.hisp.dhis.android.sdk.persistence.migrations.version17;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement$Table;


@Migration(version = 17, databaseName = Dhis2Database.NAME)
public class Version17MigrationCreateProgramStageField extends
        AlterTableMigration<ProgramRuleAction> {

    public Version17MigrationCreateProgramStageField(Class<ProgramRuleAction> table) {
        super(ProgramRuleAction.class);
    }

    public Version17MigrationCreateProgramStageField() {
        super(ProgramRuleAction.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(ProgramRuleAction.class,
                ProgramRuleAction$Table.PROGRAMSTAGE)) {
            addColumn(Boolean.class, ProgramRuleAction$Table.PROGRAMSTAGE);
        }
    }
}
