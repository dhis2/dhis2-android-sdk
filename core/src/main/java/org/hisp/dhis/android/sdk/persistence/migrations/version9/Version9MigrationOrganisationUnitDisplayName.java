package org.hisp.dhis.android.sdk.persistence.migrations.version9;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit$Table;

@Migration(version = 9, databaseName = Dhis2Database.NAME)
public class Version9MigrationOrganisationUnitDisplayName extends AlterTableMigration<OrganisationUnit> {

    public Version9MigrationOrganisationUnitDisplayName(Class<OrganisationUnit> table) {
        super(OrganisationUnit.class);
    }

    public Version9MigrationOrganisationUnitDisplayName() {
        super(OrganisationUnit.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(OrganisationUnit.class, OrganisationUnit$Table.DISPLAYNAME)) {
            addColumn(String.class, OrganisationUnit$Table.DISPLAYNAME);
        }
    }
}
