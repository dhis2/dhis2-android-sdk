package org.hisp.dhis.android.testapp.common;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.ValueTypeDeviceRendering;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ValueTypeDeviceRenderingPublicAccessShould extends BasePublicAccessShould<ValueTypeDeviceRendering> {

    @Mock
    private ValueTypeDeviceRendering object;

    @Override
    public ValueTypeDeviceRendering object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        ValueTypeDeviceRendering.create(null);
    }

    @Override
    public void has_public_builder_method() {
        ValueTypeDeviceRendering.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}