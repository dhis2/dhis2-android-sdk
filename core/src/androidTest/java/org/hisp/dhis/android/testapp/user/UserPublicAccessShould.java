package org.hisp.dhis.android.testapp.user;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class UserPublicAccessShould extends BasePublicAccessShould<User> {

    @Mock
    private User object;

    @Override
    public User object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        User.create(null);
    }

    @Override
    public void has_public_builder_method() {
        User.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}