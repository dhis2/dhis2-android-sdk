package org.hisp.dhis.client.sdk.core.organisationunit;

import org.hisp.dhis.client.sdk.core.commons.DbContract.BodyColumn;
import org.hisp.dhis.client.sdk.core.commons.DbContract.NameableColumns;
import org.hisp.dhis.client.sdk.core.commons.IdentifiableObjectStore;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

public interface OrganisationUnitStore extends IdentifiableObjectStore<OrganisationUnit> {

    List<OrganisationUnit> query(String parentOrganisationUnitId);
}
