package org.hisp.dhis.client.sdk.android.user;

import org.hisp.dhis.client.sdk.models.user.UserAccount;

import rx.Observable;

public interface UserAccountInteractor {

    Observable<UserAccount> sync();

    Observable<UserAccount> pull();

    Observable<UserAccount> push();

    Observable<UserAccount> get();

    Observable<Boolean> save(UserAccount userAccount);
}
