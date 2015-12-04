package org.hisp.dhis.android.sdk.user;

import org.hisp.dhis.java.sdk.models.user.UserAccount;

public interface IUserAccountScope {
    UserAccount signIn();
}
