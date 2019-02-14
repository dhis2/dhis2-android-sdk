package org.hisp.dhis.android.testapp.trackedentity;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityInstancePublicAccessShould extends BasePublicAccessShould<TrackedEntityInstance> {

    @Mock
    private TrackedEntityInstance object;

    @Override
    public TrackedEntityInstance object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        TrackedEntityInstance.create(null);
    }

    @Override
    public void has_public_builder_method() {
        TrackedEntityInstance.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}