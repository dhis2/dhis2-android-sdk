package org.hisp.dhis.android.testapp.user;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class UserCredentialsPublicAccessShould extends BasePublicAccessShould<UserCredentials> {

    @Mock
    private UserCredentials object;

    @Override
    public UserCredentials object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        UserCredentials.create(null);
    }

    @Override
    public void has_public_builder_method() {
        UserCredentials.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}