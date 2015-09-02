package org.hisp.dhis.android.sdk.models.user;

import org.hisp.dhis.android.sdk.models.common.IService;

/**
 * Created by arazabishov on 8/27/15.
 */
public interface IUserAccountService extends IService {
    UserAccount getCurrentUserAccount();

    User toUser(UserAccount userAccount);

    void logOut();
}
