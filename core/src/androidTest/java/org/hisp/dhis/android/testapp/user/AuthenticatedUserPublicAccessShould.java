package org.hisp.dhis.android.testapp.user;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.user.AuthenticatedUser;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class AuthenticatedUserPublicAccessShould extends BasePublicAccessShould<AuthenticatedUser> {

    @Mock
    private AuthenticatedUser object;

    @Override
    public AuthenticatedUser object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        AuthenticatedUser.create(null);
    }

    @Override
    public void has_public_builder_method() {
        AuthenticatedUser.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}