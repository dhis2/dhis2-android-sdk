package org.hisp.dhis.android.sdk.persistence.migrations.version16;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator$Table;


@Migration(version = 16, databaseName = Dhis2Database.NAME)
public class Version16MigrationCreateProgramIndicatorField extends
        AlterTableMigration<ProgramIndicator> {

    public Version16MigrationCreateProgramIndicatorField(Class<ProgramIndicator> table) {
        super(ProgramIndicator.class);
    }

    public Version16MigrationCreateProgramIndicatorField() {
        super(ProgramIndicator.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(ProgramIndicator.class,
                ProgramIndicator$Table.DISPLAYINFORM)) {
            addColumn(Boolean.class, ProgramIndicator$Table.DISPLAYINFORM);
        }
    }
}
