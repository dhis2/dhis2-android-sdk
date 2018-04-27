package org.hisp.dhis.android.sdk.persistence.migrations.version19;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.migrations.MigrationUtil;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit$Table;


@Migration(version = 19, databaseName = Dhis2Database.NAME)
public class Version19MigrationAddOrganisationUnitCode extends
        AlterTableMigration<OrganisationUnit> {

    public Version19MigrationAddOrganisationUnitCode(Class<OrganisationUnit> table) {
        super(OrganisationUnit.class);
    }

    public Version19MigrationAddOrganisationUnitCode() {
        super(OrganisationUnit.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        if (!MigrationUtil.columnExists(OrganisationUnit.class,
                OrganisationUnit$Table.CODE)) {
            addColumn(String.class, OrganisationUnit$Table.CODE);
        }
    }
}
