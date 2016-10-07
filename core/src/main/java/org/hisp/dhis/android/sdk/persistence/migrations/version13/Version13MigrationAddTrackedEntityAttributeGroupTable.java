package org.hisp.dhis.android.sdk.persistence.migrations.version13;

import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeGroup$Adapter;

@Migration(version = 13, databaseName = Dhis2Database.NAME)
public class Version13MigrationAddTrackedEntityAttributeGroupTable extends BaseMigration {
    @Override
    public void migrate(SQLiteDatabase database) {
        TrackedEntityAttributeGroup$Adapter adapter = new TrackedEntityAttributeGroup$Adapter();
        database.execSQL(adapter.getCreationQuery());
    }
}
