package org.hisp.dhis.android.testapp.trackedentity;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributeValuePublicAccessShould extends BasePublicAccessShould<TrackedEntityAttributeValue> {

    @Mock
    private TrackedEntityAttributeValue object;

    @Override
    public TrackedEntityAttributeValue object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        TrackedEntityAttributeValue.create(null);
    }

    @Override
    public void has_public_builder_method() {
        TrackedEntityAttributeValue.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}