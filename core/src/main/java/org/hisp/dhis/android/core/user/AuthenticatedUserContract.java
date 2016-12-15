package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.common.BaseModelContract;

public final class AuthenticatedUserContract {
    public interface Columns extends BaseModelContract.Columns {
        String USER = "user";
        String CREDENTIALS = "credentials";
    }
}
