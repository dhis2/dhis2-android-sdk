package org.hisp.dhis.client.sdk.core.user;

import org.hisp.dhis.client.sdk.core.commons.DbContract.IdentifiableColumns;
import org.hisp.dhis.client.sdk.core.commons.DbContract.BodyColumn;
import org.hisp.dhis.client.sdk.core.commons.IdentifiableObjectStore;
import org.hisp.dhis.client.sdk.models.user.User;

public interface UserStore extends IdentifiableObjectStore<User> {
    interface UserColumns extends IdentifiableColumns, BodyColumn {
        String TABLE_NAME = "users";
    }
}
