package org.hisp.dhis.android.sdk.persistence.migrations.version6;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;

@Migration(version = 6, databaseName = Dhis2Database.NAME)
public class Version6AddCodeDataelement extends AlterTableMigration<DataElement> {

    public Version6AddCodeDataelement(Class<DataElement> table) {
        super(DataElement.class);
    }

    public Version6AddCodeDataelement() {
        super(DataElement.class);
    }

    @Override
    public void onPreMigrate() {
        if (!MigrationUtil.columnExists(DataElement.class, "type")) {
            addColumn(String.class, "code");
        }
    }
}
