package org.hisp.dhis.android.sdk.persistence.migrations.version14;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute$Table;

@Migration(version = 17, databaseName = Dhis2Database.NAME)
public class Version17MigrationCreatePteaRenderOptionsAsRadioField extends
        AlterTableMigration<ProgramTrackedEntityAttribute> {

    public Version17MigrationCreatePteaRenderOptionsAsRadioField(Class<ProgramTrackedEntityAttribute> table) {
        super(ProgramTrackedEntityAttribute.class);
    }

    public Version17MigrationCreatePteaRenderOptionsAsRadioField() {
        super(ProgramTrackedEntityAttribute.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(ProgramTrackedEntityAttribute.class,
                ProgramTrackedEntityAttribute$Table.RENDEROPTIONSASRADIO)) {
            addColumn(Boolean.class, ProgramTrackedEntityAttribute$Table.RENDEROPTIONSASRADIO);
        }
    }
}
