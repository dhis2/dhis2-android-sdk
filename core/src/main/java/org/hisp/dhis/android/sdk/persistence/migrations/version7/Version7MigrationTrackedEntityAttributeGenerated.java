package org.hisp.dhis.android.sdk.persistence.migrations.version7;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;

@Migration(version = 7, databaseName = Dhis2Database.NAME)
public class Version7MigrationTrackedEntityAttributeGenerated  extends AlterTableMigration<TrackedEntityAttribute> {

    public Version7MigrationTrackedEntityAttributeGenerated(Class<TrackedEntityAttribute> table) {
        super(TrackedEntityAttribute.class);
    }

    public Version7MigrationTrackedEntityAttributeGenerated() {
        super(TrackedEntityAttribute.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        addColumn(Boolean.class, "generated");
        addColumn(String.class, "pattern");
    }
}
