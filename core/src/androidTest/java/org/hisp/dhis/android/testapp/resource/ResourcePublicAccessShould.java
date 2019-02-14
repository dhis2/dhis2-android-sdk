package org.hisp.dhis.android.testapp.resource;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.resource.Resource;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ResourcePublicAccessShould extends BasePublicAccessShould<Resource> {

    @Mock
    private Resource object;

    @Override
    public Resource object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        Resource.create(null);
    }

    @Override
    public void has_public_builder_method() {
        Resource.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}