package org.hisp.dhis.android.sdk.user;

import org.hisp.dhis.java.sdk.models.user.UserAccount;

import rx.Observable;

public interface IUserAccountScope {
    Observable<UserAccount> signIn(final String username, final String password);

    Observable<Boolean> signOut();
}
