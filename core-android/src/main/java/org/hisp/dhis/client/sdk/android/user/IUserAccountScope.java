package org.hisp.dhis.client.sdk.android.user;


import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.user.UserAccount;

import java.util.List;

import rx.Observable;

public interface IUserAccountScope {

    Observable<UserAccount> signIn(Configuration configuration, String username, String password);

    Observable<Boolean> signOut();


    // TODO move this methods to corresponding controllers and scopes.
    Observable<List<Program>> listAssignedPrograms(OrganisationUnit organisationUnit);

    Observable<List<OrganisationUnit>> listAssignedOrganisationUnits();

    Observable<List<OrganisationUnit>> synchronizeAssignedPrograms();
}
