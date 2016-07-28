package org.hisp.dhis.android.sdk.persistence.migrations.version7;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;

@Migration(version = 7, databaseName = Dhis2Database.NAME)
public class Version7MigrationOrganisationUnitType  extends AlterTableMigration<OrganisationUnit> {

    public Version7MigrationOrganisationUnitType(Class<OrganisationUnit> table) {
        super(OrganisationUnit.class);
    }

    public Version7MigrationOrganisationUnitType() {
        super(OrganisationUnit.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(OrganisationUnit.class, "type")) {
            addColumn(String.class, "type");
        }
    }
}
