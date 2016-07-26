package org.hisp.dhis.android.sdk.persistence.migrations.version7;

import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;

public class Version7MigrationOrganisationUnitType  extends AlterTableMigration<OrganisationUnit> {


    public Version7MigrationOrganisationUnitType(Class<OrganisationUnit> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        addColumn(String.class, "type");
    }
}
