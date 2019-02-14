package org.hisp.dhis.android.testapp.user;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.user.Authority;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class AuthorityPublicAccessShould extends BasePublicAccessShould<Authority> {

    @Mock
    private Authority object;

    @Override
    public Authority object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        Authority.create(null);
    }

    @Override
    public void has_public_builder_method() {
        Authority.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}