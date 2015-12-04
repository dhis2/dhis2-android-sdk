package org.hisp.dhis.android.sdk.user;

import org.hisp.dhis.java.sdk.common.network.ApiException;
import org.hisp.dhis.java.sdk.models.user.UserAccount;
import org.hisp.dhis.java.sdk.user.IUserAccountController;

public class UserAccountScope implements IUserAccountScope {
    private final IUserAccountController mUserAccountController;

    public UserAccountScope(IUserAccountController userAccountController) {
        mUserAccountController = userAccountController;
    }

    @Override
    public UserAccount signIn() throws ApiException {
        return mUserAccountController.updateAccount();
    }
}
