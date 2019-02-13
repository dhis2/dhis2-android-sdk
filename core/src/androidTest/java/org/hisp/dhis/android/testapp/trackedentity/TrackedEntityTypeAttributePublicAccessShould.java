package org.hisp.dhis.android.testapp.trackedentity;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttribute;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityTypeAttributePublicAccessShould extends BasePublicAccessShould<TrackedEntityTypeAttribute> {

    @Mock
    private TrackedEntityTypeAttribute object;

    @Override
    public TrackedEntityTypeAttribute object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        TrackedEntityTypeAttribute.create(null);
    }

    @Override
    public void has_public_builder_method() {
        TrackedEntityTypeAttribute.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}