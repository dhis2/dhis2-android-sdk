package org.hisp.dhis.android.sdk.user;

import org.hisp.dhis.java.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.java.sdk.models.program.Program;
import org.hisp.dhis.java.sdk.models.user.UserAccount;

import java.util.List;

import rx.Observable;

public interface IUserAccountScope {
    Observable<UserAccount> signIn(final String username, final String password);

    Observable<Boolean> signOut();

    Observable<List<Program>> listAssignedPrograms(OrganisationUnit organisationUnit);

    Observable<List<OrganisationUnit>> listAssignedOrganisationUnits();

    Observable<List<OrganisationUnit>> synchronizeAssignedPrograms();
}
