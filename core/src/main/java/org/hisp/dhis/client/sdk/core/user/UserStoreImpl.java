package org.hisp.dhis.client.sdk.core.user;

import android.content.ContentResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.commons.database.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.models.user.User;

class UserStoreImpl extends AbsIdentifiableObjectStore<User> implements UserStore {

    UserStoreImpl(ContentResolver contentResolver, ObjectMapper objectMapper) {
        super(contentResolver, new UserMapper(objectMapper));
    }
}
