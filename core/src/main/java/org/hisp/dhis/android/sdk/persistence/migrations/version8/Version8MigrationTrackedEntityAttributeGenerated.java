package org.hisp.dhis.android.sdk.persistence.migrations.version8;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;

@Migration(version = 8, databaseName = Dhis2Database.NAME)
public class Version8MigrationTrackedEntityAttributeGenerated extends AlterTableMigration<TrackedEntityAttribute> {

    public Version8MigrationTrackedEntityAttributeGenerated(Class<TrackedEntityAttribute> table) {
        super(TrackedEntityAttribute.class);
    }

    public Version8MigrationTrackedEntityAttributeGenerated() {
        super(TrackedEntityAttribute.class);
    }



    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(TrackedEntityAttribute.class, "generated")) {
            addColumn(Boolean.class, "generated");
        }
        if (!MigrationUtil.columnExists(TrackedEntityAttribute.class, "pattern")) {
            addColumn(String.class, "pattern");
        }
    }
}