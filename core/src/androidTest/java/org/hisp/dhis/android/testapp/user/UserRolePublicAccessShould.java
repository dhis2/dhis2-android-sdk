package org.hisp.dhis.android.testapp.user;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.user.UserRole;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class UserRolePublicAccessShould extends BasePublicAccessShould<UserRole> {

    @Mock
    private UserRole object;

    @Override
    public UserRole object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        UserRole.create(null);
    }

    @Override
    public void has_public_builder_method() {
        UserRole.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}