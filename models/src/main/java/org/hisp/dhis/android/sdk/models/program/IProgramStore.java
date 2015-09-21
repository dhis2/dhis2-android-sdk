package org.hisp.dhis.android.sdk.models.program;

import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

public interface IProgramStore extends IIdentifiableObjectStore<Program> {
    public List<Program> query(OrganisationUnit organisationUnit);
    public void assign(Program program, List<OrganisationUnit> organisationUnits);
}
