package org.hisp.dhis.client.sdk.core.organisationunit;

import org.hisp.dhis.client.sdk.core.commons.DbContract.BodyColumn;
import org.hisp.dhis.client.sdk.core.commons.DbContract.NameableColumns;
import org.hisp.dhis.client.sdk.core.commons.IdentifiableObjectStore;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

public interface OrganisationUnitStore extends IdentifiableObjectStore<OrganisationUnit> {
    interface OrganisationUnitColumns extends NameableColumns, BodyColumn {
        String TABLE_NAME = "organisationUnits";
        String COLUMN_PARENT = "parent";
        String COLUMN_OPENING_DATE = "openingDate";
        String COLUMN_CLOSED_DATE = "closedDate";
        String COLUMN_LEVEL = "level";
    }
}
