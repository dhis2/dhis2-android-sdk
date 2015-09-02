package org.hisp.dhis.android.sdk.models.user;

import org.hisp.dhis.android.sdk.models.common.IModelsStore;

import java.util.List;

/**
 * Created by arazabishov on 8/27/15.
 */
public class UserAccountService implements IUserAccountService {
    private final IUserAccountStore userAccountStore;
    private final IModelsStore modelsStore;

    public UserAccountService(IUserAccountStore userAccountStore, IModelsStore modelsStore) {
        this.userAccountStore = userAccountStore;
        this.modelsStore = modelsStore;
    }

    @Override
    public UserAccount getCurrentUserAccount() {
        List<UserAccount> userAccounts = userAccountStore.query();
        return userAccounts != null && !userAccounts.isEmpty() ? userAccounts.get(0) : null;
    }

    @Override
    public User toUser(UserAccount userAccount) {
        User user = new User();
        user.setUId(userAccount.getUId());
        user.setAccess(userAccount.getAccess());
        user.setCreated(user.getCreated());
        user.setLastUpdated(userAccount.getLastUpdated());
        user.setName(userAccount.getName());
        user.setDisplayName(userAccount.getDisplayName());
        return user;
    }

    @Override
    public void logOut() {

        // removing all existing data
        modelsStore.deleteAllTables();
    }
}
