package org.hisp.dhis.client.sdk.core.organisationunit;

import android.content.ContentResolver;

import org.hisp.dhis.client.sdk.core.commons.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

public class OrganisationUnitStoreImpl extends AbsIdentifiableObjectStore<OrganisationUnit> {
    public OrganisationUnitStoreImpl(ContentResolver contentResolver, Mapper<OrganisationUnit> mapper) {
        super(contentResolver, mapper);
    }
}
