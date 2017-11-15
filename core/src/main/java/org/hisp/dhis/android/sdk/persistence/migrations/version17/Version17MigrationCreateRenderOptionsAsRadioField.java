package org.hisp.dhis.android.sdk.persistence.migrations.version17;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement;


@Migration(version = 17, databaseName = Dhis2Database.NAME)
public class Version17MigrationCreateRenderOptionsAsRadioField extends
        AlterTableMigration<ProgramStageDataElement> {

    public Version17MigrationCreateRenderOptionsAsRadioField(Class<ProgramStageDataElement> table) {
        super(ProgramStageDataElement.class);
    }

    public Version17MigrationCreateRenderOptionsAsRadioField() {
        super(ProgramStageDataElement.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(ProgramStageDataElement.class,
                ProgramStageDataElement$Table.RENDEROPTIONSASRADIO)) {
            addColumn(Boolean.class, ProgramStageDataElement$Table.RENDEROPTIONSASRADIO);
        }
    }
}
