package org.hisp.dhis.android.testapp.trackedentity;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityTypePublicAccessShould extends BasePublicAccessShould<TrackedEntityType> {

    @Mock
    private TrackedEntityType object;

    @Override
    public TrackedEntityType object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        TrackedEntityType.create(null);
    }

    @Override
    public void has_public_builder_method() {
        TrackedEntityType.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}