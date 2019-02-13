package org.hisp.dhis.android.testapp.trackedentity;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributePublicAccessShould extends BasePublicAccessShould<TrackedEntityAttribute> {

    @Mock
    private TrackedEntityAttribute object;

    @Override
    public TrackedEntityAttribute object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        TrackedEntityAttribute.create(null);
    }

    @Override
    public void has_public_builder_method() {
        TrackedEntityAttribute.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}