package org.hisp.dhis.client.sdk.android.user;


import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.models.user.UserAccount;

import rx.Observable;

public interface IUserAccountScope {

    Observable<UserAccount> signIn(Configuration configuration, String username, String password);

    Observable<Boolean> isSignedIn();

    Observable<Boolean> signOut();
}
