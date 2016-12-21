package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectContract;

public final class UserCredentialsContract {
    public interface Columns extends BaseIdentifiableObjectContract.Columns {
        String USERNAME = "username";
        String USER = "user";
    }
}
