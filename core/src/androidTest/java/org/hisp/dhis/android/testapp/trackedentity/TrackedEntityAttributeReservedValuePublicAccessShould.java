package org.hisp.dhis.android.testapp.trackedentity;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValue;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributeReservedValuePublicAccessShould
        extends BasePublicAccessShould<TrackedEntityAttributeReservedValue> {

    @Mock
    private TrackedEntityAttributeReservedValue object;

    @Override
    public TrackedEntityAttributeReservedValue object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        TrackedEntityAttributeReservedValue.create(null);
    }

    @Override
    public void has_public_builder_method() {
        TrackedEntityAttributeReservedValue.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}