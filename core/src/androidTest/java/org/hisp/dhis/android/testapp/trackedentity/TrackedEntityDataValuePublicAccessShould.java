package org.hisp.dhis.android.testapp.trackedentity;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityDataValuePublicAccessShould extends BasePublicAccessShould<TrackedEntityDataValue> {

    @Mock
    private TrackedEntityDataValue object;

    @Override
    public TrackedEntityDataValue object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        TrackedEntityDataValue.create(null);
    }

    @Override
    public void has_public_builder_method() {
        TrackedEntityDataValue.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}