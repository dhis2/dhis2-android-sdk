package org.hisp.dhis.client.sdk.android.organisationunit;

import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

import rx.Observable;

public interface IOrganisationUnitScope {

    Observable<Boolean> save(OrganisationUnit organisationUnit);

    Observable<Boolean> remove(OrganisationUnit organisationUnit);

    Observable<OrganisationUnit> get(long id);

    Observable<OrganisationUnit> get(String uid);

    Observable<List<OrganisationUnit>> list();
}
